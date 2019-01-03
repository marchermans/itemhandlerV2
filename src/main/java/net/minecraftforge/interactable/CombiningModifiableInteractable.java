package net.minecraftforge.interactable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.interactable.api.*;
import net.minecraftforge.interactable.api.observer.IObserverWatchDog;
import net.minecraftforge.interactable.api.observer.ObserveableWatchDog;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
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
 * @param <T> The type contained in the wrapped modifiable interactables.
 */
public class CombiningModifiableInteractable<T> extends CombiningInteractable<T> implements IModifiableInteractable<T> {

    private CombiningInteractableTransaction<T> activeTransaction;
    private final Map<ResourceLocation, BiConsumer<IModifiableInteractable<T>, Set<Integer>>> observers = new HashMap<>();
    private final List<IModifiableInteractable<T>> readWriteInteractables;
    private final List<IObserverWatchDog<T>> openWatchDogs = new ArrayList<>();

    /**
     * This field is set by the transaction to disable change passthrough when changes are being committed.
     * prevents observers from this interactable from seeing an invalid partially committed state.
     */
    private boolean holdChangedSlots = false;

    public CombiningModifiableInteractable(List<IModifiableInteractable<T>> iModifiableInteractables) {
        super(iModifiableInteractables);
        this.readWriteInteractables = iModifiableInteractables;
    }

    @Override
    public final IInteractableTransaction<T> beginTransaction() {
        this.activeTransaction = buildNewTransaction();
        return activeTransaction;
    }

    protected CombiningInteractableTransaction<T> buildNewTransaction() {
        return new CombiningInteractableTransaction<>(this);
    }

    @Override
    public final boolean isActiveTransaction(IInteractableTransaction<T> transactionToCheck) {
        return activeTransaction == transactionToCheck;
    }


    @Override
    public final IObserverWatchDog<T> openObserver(ResourceLocation id, BiConsumer<IModifiableInteractable<T>, Set<Integer>> callback) throws IllegalArgumentException {
        if (observers.containsKey(id))
            throw new IllegalArgumentException(String.format("Observer with ID: %s is already registered.", id));

        observers.put(id, callback);

        //Add an observer to our sub handlers. That way we can passthrough any events if needed.
        if (observers.size() == 1) {
            for (final IModifiableInteractable<T> interactable :
                    this.readWriteInteractables) {
                openWatchDogs.add(interactable.openObserver(
                        new ResourceLocation("forge", String.format("combining%s", UUID.randomUUID())),
                        (changedInteractable, slots) -> {
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
                            for (final BiConsumer<IModifiableInteractable<T>, Set<Integer>> ownCallback :
                                    observers.values()) {
                                callback.accept(this, remappedSlots);
                            }
                        }));
            }
        }

        return new ObserveableWatchDog<>(this, id, callback);
    }

    @Override
    public final void closeObserver(ResourceLocation id) throws IllegalArgumentException {
        if (!observers.containsKey(id))
            throw new IllegalArgumentException(String.format("Observer with ID: %s is not registered", id));

        observers.remove(id);

        if (observers.size() == 0)
        {
            for (final IObserverWatchDog<T> watchDog :
                    openWatchDogs) {
                watchDog.close();
            }
        }
    }

    /**
     * The default transaction for a combining interactable.
     * It assumes that the default value for E is null, and that {@code E.equals(Object)} is a strict comparison.
     * <p>
     * If E has a none default value (EG default value for E is not null) override the {@link #isInstancePresent(Object)} method.
     * <p>
     * If E has a none strict equals behaviour (EG default equals behaviour, is instance check, or amount is not taken into account)
     * then override the {@link #didInsertGetModified(E, E)} method and implement the comparison properly, watch for the negation in the
     * contract!
     *
     * @param <E> The type that is being stored in the interactable on which this transaction operates.
     * @see net.minecraftforge.interactable.itemhandler.CombiningModifiableItemHandler.CombiningItemHandlerTransaction
     * @see net.minecraftforge.interactable.fluidhandler.CombiningModifiableFluidHandler.CombiningFluidHandlerTransaction
     */
    public static class CombiningInteractableTransaction<E> extends CombiningInteractable<E> implements IInteractableTransaction<E> {

        private final CombiningModifiableInteractable<E> readWriteInteractable;
        private final List<IInteractableTransaction<E>> internalTransactionHandlers;
        private final Set<Integer> modifiedSlots = new HashSet<>();

        public CombiningInteractableTransaction(final CombiningModifiableInteractable<E> readWriteInteractable) {
            super(readWriteInteractable.readWriteInteractables.stream().map(IModifiableInteractable::beginTransaction).collect(Collectors.toList()));
            this.readWriteInteractable = readWriteInteractable;
            this.internalTransactionHandlers = super.readOnlyInteractables.stream().map(interactable -> (IInteractableTransaction<E>) interactable).collect(Collectors.toList());
        }

        /**
         * Cancels the current transaction.
         */
        @Override
        public final void cancel() {
            if (readWriteInteractable.isActiveTransaction(this))
                readWriteInteractable.activeTransaction = null;

            internalTransactionHandlers.forEach(IInteractableTransaction::cancel);
        }

        /**
         * Attempts to commit the transaction.
         * If this method is called on a not active transaction, indicated by {@link IModifiableInteractable#isActiveTransaction(IInteractableTransaction)}
         * being false, then an exception is thrown.
         *
         * @throws TransactionNotValidException When this transaction is not the active transaction.
         */
        @Override
        public final void commit() throws TransactionNotValidException {
            if (!readWriteInteractable.isActiveTransaction(this))
                throw new TransactionNotValidException(readWriteInteractable, this);

            //First verify that each transaction is still active:
            for (int i = 0; i < this.internalTransactionHandlers.size(); i++) {
                if (!readWriteInteractable.readWriteInteractables.get(i).isActiveTransaction(this.internalTransactionHandlers.get(i)))
                    throw new TransactionNotValidException(readWriteInteractable, this);
            }

            //Notify our interactable that we are about to commit changes to his
            //Subhandlers. He should hold the modified slots and let us trigger the
            //Event in one go.
            readWriteInteractable.holdChangedSlots = true;

            for (final IInteractableTransaction<E> internalTransactionHandler : internalTransactionHandlers) {
                internalTransactionHandler.commit();
            }

            readWriteInteractable.holdChangedSlots = false;

            //Trigger observables.
            for (final BiConsumer<IModifiableInteractable<E>, Set<Integer>> callback :
                    readWriteInteractable.observers.values()) {
                callback.accept(readWriteInteractable, this.modifiedSlots);
            }

            this.modifiedSlots.clear();
        }

        /**
         * Attempts to insert the given instance into the slot of this interactable.
         *
         * @param slot     The slot to insert into.
         * @param toInsert The object to insert.
         * @return An instance of {@link IInteractableOperationResult} that indicates success or failure, and provides results.
         */
        @Override
        public IInteractableOperationResult<E> insert(final int slot, final E toInsert) {
            final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);
            return this.internalTransactionHandlers.get(targets.getFirst()).insert(targets.getSecond(), toInsert);
        }

        @Override
        public IInteractableOperationResult<E> insert(final E toInsert) {
            boolean wasConflicted = false;
            E remainderToInsert = toInsert;
            for (final IInteractableTransaction<E> internalTransaction :
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

        /**
         * This method checks if the given instance is present in terms of the semantics of the type E.
         * For example for ItemStacks this would say {@code instance != null && !instance.isEmpty()}.
         * <p>
         * Default implementation does a nonnull check: {@code instance != null};
         *
         * @param instance The instance to check.
         * @return True when for type E a valid object is present, false when not.
         */
        protected boolean isInstancePresent(E instance) {
            return instance != null;
        }

        /**
         * This method should check if the given two instance differ from each other exactly.
         * <p>
         * This method is called by the insertion logic for insertion without slot, and should
         * check if insertion was even possible (So if a partial entry was inserted or not).
         * If both entries are exactly the same, including amount, then false needs to be returned
         * and the insertion will have failed.
         * <p>
         * In any other case return true, and the insertion will either check for conflicting
         * or successful insertion (both partial an full are taken care of for both cases).
         * <p>
         * Default implementation calls {@link Object#equals(Object)} on the toInsert instance.
         *
         * @param toInsert          The instance that is being inserted for slotless insertion.
         * @param remainingToInsert The instance that is left over after all handlers have been tried.,
         * @return True when {@code toInsert != remainingToInsert} (exact check required), false when not.
         */
        protected boolean didInsertGetModified(E toInsert, E remainingToInsert) {
            return !toInsert.equals(remainingToInsert);
        }

        /**
         * Attempts to extract a given amount from the slot of this interactable.
         *
         * @param slot   The slot to extract from.
         * @param amount The amount to extract.
         * @return An instance of {@link IInteractableOperationResult} that indicates success or failure, and provides results.
         */
        @Override
        public IInteractableOperationResult<E> extract(final int slot, final int amount) {
            final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);
            return this.internalTransactionHandlers.get(targets.getFirst()).extract(targets.getSecond(), amount);
        }

        /**
         * The interactable which is being manipulated, once {@link #commit()} is called.
         *
         * @return The interactable.
         */
        @Override
        public final IModifiableInteractable<E> getInteractable() {
            return readWriteInteractable;
        }
    }
}
