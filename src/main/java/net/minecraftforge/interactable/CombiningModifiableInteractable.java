package net.minecraftforge.interactable;

import net.minecraft.util.Tuple;
import net.minecraftforge.interactable.api.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A modifiable interactable that combines several interactables into one.
 *
 * This class can be used as long as the default value for the type E is null, as well as
 * the equals check is exact.
 *
 * If this is not the case subclass this class and its transaction type.
 * Override the {@link #buildNewTransaction()} and return your transaction instance with the
 * correct methods overriden for the type E.
 *
 * @param <T> The type contained in the wrapped modifiable interactables.
 */
public class CombiningModifiableInteractable<T> extends CombiningInteractable<T> implements IModifiableInteractable<T> {

    private List<IModifiableInteractable<T>> readWriteInteractables;
    private IInteractableTransaction<T> activeTransaction;

    public CombiningModifiableInteractable(List<IModifiableInteractable<T>> iModifiableInteractables) {
        super(iModifiableInteractables);
        this.readWriteInteractables = iModifiableInteractables;
    }

    /**
     * Begins a new transaction for this interactable.
     * <p>
     * Either call {@link IInteractableTransaction#commit()}
     * to commit the transaction and make it live, or call {@link IInteractableTransaction#cancel()}
     * to cancel the transaction.
     *
     * @return The transaction object to handle the transaction.
     */
    @Override
    public final IInteractableTransaction<T> beginTransaction() {
        this.activeTransaction = buildNewTransaction();
        return activeTransaction;
    }

    /**
     * Method used to build a new transaction.
     * Can be overriden by subclasses to return different transactions with different behaviours.
     *
     * @return The new transaction, about to become the active transaction.
     */
    protected CombiningInteractableTransaction<T> buildNewTransaction()
    {
        return new CombiningInteractableTransaction<>(this);
    }

    /**
     * Checks if the given transaction is the active one.
     *
     * @param transactionToCheck The transaction to check.
     * @return True when the given transaction is active and can be commited, false when not.
     */
    @Override
    public final boolean isActiveTransaction(IInteractableTransaction<T> transactionToCheck) {
        return activeTransaction == transactionToCheck;
    }

    /**
     * The default transaction for a combining interactable.
     * It assumes that the default value for E is null, and that {@code E.equals(Object)} is a strict comparison.
     *
     * If E has a none default value (EG default value for E is not null) override the {@link #isInstancePresent(Object)} method.
     *
     * If E has a none strict equals behaviour (EG default equals behaviour, is instance check, or amount is not taken into account)
     * then override the {@link #didInsertGetModified(E, E)} method and implement the comparison properly, watch for the negation in the
     * contract!
     *
     * @param <E> The type that is being stored in the interactable on which this transaction operates.
     *
     * @see net.minecraftforge.interactable.itemhandler.CombiningModifiableItemHandler.CombiningItemHandlerTransaction
     * @see net.minecraftforge.interactable.fluidhandler.CombiningModifiableFluidHandler.CombiningFluidHandlerTransaction
     */
    public class CombiningInteractableTransaction<E> extends CombiningInteractable<E> implements IInteractableTransaction<E>
    {

        private final CombiningModifiableInteractable<E> readWriteInteractable;
        private final List<IInteractableTransaction<E>> internalTransactionHandlers;

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

            for (final IInteractableTransaction<E> internalTransactionHandler : internalTransactionHandlers) {
                internalTransactionHandler.commit();
            }
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
                if (interactionResult.wasSuccessful())
                {
                    remainderToInsert = interactionResult.getPrimary();
                }
                else if (interactionResult.getStatus().isConflicting())
                {
                    wasConflicted = true;
                    if (isInstancePresent(interactionResult.getPrimary()))
                    {
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
         *
         * Default implementation does a nonnull check: {@code instance != null};
         *
         * @param instance The instance to check.
         * @return True when for type E a valid object is present, false when not.
         */
        protected boolean isInstancePresent(E instance)
        {
            return instance != null;
        }

        /**
         * This method should check if the given two instance differ from each other exactly.
         *
         * This method is called by the insertion logic for insertion without slot, and should
         * check if insertion was even possible (So if a partial entry was inserted or not).
         * If both entries are exactly the same, including amount, then false needs to be returned
         * and the insertion will have failed.
         *
         * In any other case return true, and the insertion will either check for conflicting
         * or successful insertion (both partial an full are taken care of for both cases).
         *
         * Default implementation calls {@link Object#equals(Object)} on the toInsert instance.
         *
         * @param toInsert The instance that is being inserted for slotless insertion.
         * @param remainingToInsert The instance that is left over after all handlers have been tried.,
         * @return True when {@code toInsert != remainingToInsert} (exact check required), false when not.
         */
        protected boolean didInsertGetModified(E toInsert, E remainingToInsert)
        {
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
