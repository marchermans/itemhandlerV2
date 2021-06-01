package net.minecraftforge.interactable;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.interactable.api.IInteractableSearchHandler;
import net.minecraftforge.interactable.api.IModifiableSlottedInteractable;
import net.minecraftforge.interactable.api.ISlottedInteractableTransaction;
import net.minecraftforge.interactable.api.TransactionNotValidException;
import net.minecraftforge.interactable.api.observer.IModifiableSlottedInteractableChangedHandler;
import net.minecraftforge.interactable.api.observer.IObserverWatchDog;
import net.minecraftforge.interactable.observer.ObserverWatchdog;
import net.minecraftforge.util.ListWithFixedSize;

import java.util.*;

/**
 * Abstract modifiable interactable implementation that handles observables as well as the active transaction.
 *
 * @param <E> The type contained in the interactable.
 */
public abstract class ModifiableSlottedInteractable<E, T extends ISlottedInteractableTransaction<E, T>>  extends SlottedInteractable<E> implements IModifiableSlottedInteractable<E, T>
{
    private       T                                                                         activeTransaction;
    private final Map<ResourceLocation, IModifiableSlottedInteractableChangedHandler<E, T>> modifiableObservers = new HashMap<>();

    public ModifiableSlottedInteractable(int size) {
        super(size);
    }

    public ModifiableSlottedInteractable(E... iterable) {
        super(iterable);
    }

    public ModifiableSlottedInteractable(Collection<E> iterable) {
        super(iterable);
    }

    @Override
    public final T beginTransaction() {
        this.activeTransaction = buildNewTransaction();
        return activeTransaction;
    }

    /**
     * Method used to build a new transaction.
     * Can be overriden by subclasses to return different transactions with different behaviours.
     *
     * @return The new transaction, about to become the active transaction.
     */
    protected abstract T buildNewTransaction();

    @Override
    public final boolean isActiveTransaction(T transactionToCheck) {
        return activeTransaction == transactionToCheck;
    }

    @Override
    public IObserverWatchDog<E, ? extends IModifiableSlottedInteractable<E, T>> openObserver(
      final ResourceLocation id, final IModifiableSlottedInteractableChangedHandler<E, T> callback) throws IllegalArgumentException
    {
        if (modifiableObservers.containsKey(id))
            throw new IllegalArgumentException(String.format("Observer with ID: %s is already registered.", id));

        modifiableObservers.put(id, callback);

        return new ObserverWatchdog<>(this, id);
    }

    @Override
    public final void closeObserver(ResourceLocation id) throws IllegalArgumentException {
        if (!modifiableObservers.containsKey(id))
            throw new IllegalArgumentException(String.format("Observer with ID: %s is not registered", id));

        modifiableObservers.remove(id);
    }

    /**
     * An abstract transaction implementation that handles the modified slots as well as
     * commit and cancel operations. Fires the changed event when committed.
     *
     * Implementers need to call {@link #onSlotInteracted(int)} to indicate that a slot
     * has been interacted with and that it has to be passed on when the changed event is fired.
     *
     * @param <E> The type contained in the transaction.
     */
    @SuppressWarnings("unchecked")
    public static abstract class AbstractSlottedTransaction<E, T extends ISlottedInteractableTransaction<E, T>> extends ModifiableSlottedInteractable<E, T> implements ISlottedInteractableTransaction<E, T>
    {
        private final ModifiableSlottedInteractable<E, T> modifiableInteractable;
        private final Set<Integer>                     modifiedSlots = new HashSet<>();

        protected AbstractSlottedTransaction(ModifiableSlottedInteractable<E, T> modifiableInteractable) {
            super(modifiableInteractable.interactable);
            this.modifiableInteractable = modifiableInteractable;
        }

        @Override
        public final void cancel() {
            if (modifiableInteractable.isActiveTransaction((T) this))
                modifiableInteractable.activeTransaction = null;
        }

        @Override
        public final void commit() throws TransactionNotValidException {
            if (!modifiableInteractable.isActiveTransaction((T) this))
                throw new TransactionNotValidException(modifiableInteractable, this);

            modifiableInteractable.interactable = new ListWithFixedSize<>(interactable);

            //Trigger observables.
            for (final IModifiableSlottedInteractableChangedHandler<E, T> callback :
                    modifiableInteractable.modifiableObservers.values()) {
                callback.onChanged(modifiableInteractable, modifiedSlots);
            }

            this.modifiedSlots.clear();
        }

        /**
         * Needs to be called by {@link #insert(int, Object)}, {@link #insert(Object)},
         * {@link #extract(int, int)}, {@link #extractFirstMatching(IInteractableSearchHandler)} and {@link #extractFirstMatching(IInteractableSearchHandler, int)}
         * to ensure that the observables get the right information about which slots might have changed.
         *
         * @param slotIndex The slot that has been interacted with.
         */
        protected final void onSlotInteracted(int slotIndex)
        {
            modifiedSlots.add(slotIndex);
        }

        @Override
        public IModifiableSlottedInteractable<E, T> getInteractable()
        {
            return modifiableInteractable;
        }
    }
}
