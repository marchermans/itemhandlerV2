package net.minecraftforge.interactable.combining;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.interactable.api.*;
import net.minecraftforge.interactable.api.observer.IModifiableSlottedInteractableChangedHandler;
import net.minecraftforge.interactable.api.observer.IObserverWatchDog;
import net.minecraftforge.interactable.api.observer.ISlottedInteractableChangedHandler;
import net.minecraftforge.interactable.fluidhandler.CombiningSlottedModifiableFluidHandler;
import net.minecraftforge.interactable.itemhandler.CombiningSlottedModifiableItemHandler;
import net.minecraftforge.interactable.observer.ObserverWatchdog;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * A modifiable interactable that combines several interactables into one.
 * <p>
 * This class can be used as long as the default value for the type E is null, as well as
 * the equals check is exact.
 * <p>
 * If this is not the case subclass this class and its transaction type.
 * Override the {@link #buildNewTransaction()} and return your transaction instance with the
 * correct methods overriden for the type E.
 *
 * @param <E> The type contained in the wrapped modifiable interactables.
 */
public class CombiningModifiableSlottedInteractable<E> extends CombiningSlottedInteractable<E> implements IModifiableSlottedInteractable<E, CombiningModifiableSlottedInteractable.CombiningSlottedInteractableTransaction<E>>
{

    private       CombiningSlottedInteractableTransaction<E>                                         activeTransaction;
    private final Map<ResourceLocation, IModifiableSlottedInteractableChangedHandler<E, CombiningSlottedInteractableTransaction<E>>> observers     = new HashMap<>();
    private final List<IModifiableSlottedInteractable<E, ?>>                                            readWriteInteractables;
    private final List<IObserverWatchDog<E, ?>>                                                         openWatchDogs = new ArrayList<>();

    private boolean holdChangedSlots = false;

    public CombiningModifiableSlottedInteractable(List<IModifiableSlottedInteractable<E, ?>> iModifiableInteractables) {
        super(iModifiableInteractables);
        this.readWriteInteractables = iModifiableInteractables;
    }

    @Override
    public final CombiningSlottedInteractableTransaction<E> beginTransaction() {
        this.activeTransaction = buildNewTransaction();
        return activeTransaction;
    }

    protected CombiningSlottedInteractableTransaction<E> buildNewTransaction() {
        return new CombiningSlottedInteractableTransaction<>(this);
    }

    @Override
    public boolean isActiveTransaction(final CombiningSlottedInteractableTransaction<E> transactionToCheck)
    {
        return activeTransaction == transactionToCheck;
    }

    @Override
    public final void closeObserver(ResourceLocation id) throws IllegalArgumentException {
        if (!observers.containsKey(id))
            throw new IllegalArgumentException(String.format("Observer with ID: %s is not registered", id));

        observers.remove(id);

        if (observers.size() == 0)
        {
            for (final IObserverWatchDog<E, ?> watchDog :
                    openWatchDogs) {
                watchDog.close();
            }
        }
    }

    @Override
    public IObserverWatchDog<E, ? extends IModifiableSlottedInteractable<E, CombiningSlottedInteractableTransaction<E>>> openObserver(
      final ResourceLocation id, final IModifiableSlottedInteractableChangedHandler<E, CombiningSlottedInteractableTransaction<E>> callback) throws IllegalArgumentException
    {
        if (observers.containsKey(id))
            throw new IllegalArgumentException(String.format("Observer with ID: %s is already registered.", id));

        observers.put(id, callback);

        //Add an observer to our sub handlers. That way we can passthrough any events if needed.
        if (observers.size() == 1) {
            for (final IModifiableSlottedInteractable<E, ?> interactable :
              this.readWriteInteractables) {
                openWatchDogs.add(interactable.openObserver(
                  new ResourceLocation("forge", String.format("combining%s", UUID.randomUUID())),
                  (ISlottedInteractableChangedHandler<E>) (changedInteractable, slots) -> {
                      final int interactableOffset = getStartIndexOrSubInteractable(changedInteractable);
                      final Set<Integer> remappedSlots = slots.stream().map(i -> i + interactableOffset).collect(Collectors.toSet());

                      //Check if a commit is occuring by our transaction.
                      if (holdChangedSlots)
                      {
                          //Commit occuring by our transaction
                          //Hold the slots and prevent passthrough:
                          this.activeTransaction.modifiedSlots.addAll(remappedSlots);
                          return;
                      }

                      //Trigger observables.
                      for (final IModifiableSlottedInteractableChangedHandler<E, CombiningSlottedInteractableTransaction<E>> ownCallback :
                        observers.values()) {
                          ownCallback.onChanged(this, remappedSlots);
                      }
                  }));
            }
        }

        return new ObserverWatchdog<E, IModifiableSlottedInteractable<E, CombiningSlottedInteractableTransaction<E>>>(this, id);
    }

    public static class CombiningSlottedInteractableTransaction<E> extends CombiningSlottedInteractable<E> implements ISlottedInteractableTransaction<E, CombiningSlottedInteractableTransaction<E>>
    {

        private final CombiningModifiableSlottedInteractable<E> readWriteInteractable;
        private final List<ISlottedInteractableTransaction<E, ?>>  internalTransactionHandlers;
        private final Set<Integer>                              modifiedSlots = new HashSet<>();

        public CombiningSlottedInteractableTransaction(final CombiningModifiableSlottedInteractable<E> readWriteInteractable) {
            super(readWriteInteractable.readWriteInteractables.stream().map(IModifiableSlottedInteractable::beginTransaction).collect(Collectors.toList()));
            this.readWriteInteractable = readWriteInteractable;
            this.internalTransactionHandlers = super.readOnlySlottedInteractables.stream().map(interactable -> (ISlottedInteractableTransaction<E, ?>) interactable).collect(Collectors.toList());
        }

        @Override
        public final void cancel() {
            if (readWriteInteractable.isActiveTransaction(this))
                readWriteInteractable.activeTransaction = null;

            internalTransactionHandlers.forEach(ISlottedInteractableTransaction::cancel);
        }

        @Override
        public final void commit() throws TransactionNotValidException {
            if (!readWriteInteractable.isActiveTransaction(this))
                throw new TransactionNotValidException(readWriteInteractable, this);

            //First verify that each transaction is still active:
            for (int i = 0; i < this.internalTransactionHandlers.size(); i++) {
                if (!isActiveTransactionOnInternalInteractable( readWriteInteractable.readWriteInteractables.get(i), this.internalTransactionHandlers.get(i)))
                    throw new TransactionNotValidException(readWriteInteractable, this);
            }

            //Notify our interactable that we are about to commit changes to his
            //Subhandlers. He should hold the modified slots and let us trigger the
            //Event in one go.
            readWriteInteractable.holdChangedSlots = true;

            for (final ISlottedInteractableTransaction<E, ?> internalTransactionHandler : internalTransactionHandlers) {
                internalTransactionHandler.commit();
            }

            readWriteInteractable.holdChangedSlots = false;

            //Trigger observables.
            for (final IModifiableSlottedInteractableChangedHandler<E, CombiningSlottedInteractableTransaction<E>> callback :
                    readWriteInteractable.observers.values()) {
                callback.onChanged(readWriteInteractable, this.modifiedSlots);
            }

            this.modifiedSlots.clear();
        }

        @SuppressWarnings("unchecked")
        private static <G, H extends IInteractableTransaction<G, H>> boolean isActiveTransactionOnInternalInteractable(final IModifiableInteractable<G, H> interactable, ISlottedInteractableTransaction<?,?> transaction) {
            try {
                final H ghTransaction = (H) transaction;
                return interactable.isActiveTransaction(ghTransaction);
            } catch (ClassCastException exception) {
                return false;
            }
        }

        @Override
        public IInteractableOperationResult<E> insert(final int slot, final E toInsert) {
            final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);
            return this.internalTransactionHandlers.get(targets.getA()).insert(targets.getB(), toInsert);
        }

        @Override
        public IInteractableOperationResult<E> insert(final E toInsert) {
            boolean wasConflicted = false;
            E remainderToInsert = toInsert;
            for (final ISlottedInteractableTransaction<E, ?> internalTransaction :
                    this.internalTransactionHandlers) {
                final IInteractableOperationResult<E> interactionResult = internalTransaction.insert(remainderToInsert);
                if (interactionResult.wasSuccessful()) {
                    remainderToInsert = interactionResult.getPrimary();
                } else if (interactionResult.getStatus().isConflicting()) {
                    wasConflicted = true;
                    if (isInstancePresent(interactionResult.getPrimary())) {
                        remainderToInsert = interactionResult.getPrimary();
                    }
                }

                if (!isInstancePresent(remainderToInsert))
                    return InteractableOperationResult.success(null, null);
            }

            if (!didInsertGetModified(toInsert, remainderToInsert))
                return InteractableOperationResult.failed();

            if (wasConflicted)
                return InteractableOperationResult.conflicting(remainderToInsert);

            return InteractableOperationResult.success(remainderToInsert, null);
        }

        protected boolean isInstancePresent(E instance) {
            return instance != null;
        }

        protected boolean didInsertGetModified(E toInsert, E remainingToInsert) {
            return !toInsert.equals(remainingToInsert);
        }

        @Override
        public IInteractableOperationResult<E> extract(final int slot, final int amount) {
            final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);
            return this.internalTransactionHandlers.get(targets.getA()).extract(targets.getB(), amount);
        }

        @Override
        public IModifiableSlottedInteractable<E, CombiningSlottedInteractableTransaction<E>> getInteractable()
        {
            return readWriteInteractable;
        }
    }
}
