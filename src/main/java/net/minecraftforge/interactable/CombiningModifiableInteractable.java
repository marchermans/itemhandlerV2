package net.minecraftforge.interactable;

import net.minecraft.util.Tuple;
import net.minecraftforge.interactable.api.*;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

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

    public class CombiningInteractableTransaction<T> extends CombiningInteractable<T> implements IInteractableTransaction<T>
    {

        private final CombiningModifiableInteractable<T> readWriteInteractable;
        private final List<IInteractableTransaction<T>> internalTransactionHandlers;

        public CombiningInteractableTransaction(final CombiningModifiableInteractable<T> readWriteInteractable) {
            super(readWriteInteractable.readWriteInteractables.stream().map(IModifiableInteractable::beginTransaction).collect(Collectors.toList()));
            this.readWriteInteractable = readWriteInteractable;
            this.internalTransactionHandlers = super.readOnlyInteractables.stream().map(interactable -> (IInteractableTransaction<T>) interactable).collect(Collectors.toList());
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

            for (final IInteractableTransaction<T> internalTransactionHandler : internalTransactionHandlers) {
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
        public IInteractableOperationResult<T> insert(final int slot, final T toInsert) {
            final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);
            return this.internalTransactionHandlers.get(targets.getFirst()).insert(targets.getSecond(), toInsert);
        }

        @Override
        public IInteractableOperationResult<T> insert(final T toInsert) {
            boolean wasConflicted = false;
            T remainderToInsert = toInsert;
            for (final IInteractableTransaction<T> internalTransaction :
                    this.internalTransactionHandlers) {
                final IInteractableOperationResult<T> interactionResult = internalTransaction.insert(remainderToInsert);
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
         * This method checks if the given instance is present in terms of the semantics of the type T.
         * For example for ItemStacks this would say {@code instance != null && !instance.isEmpty()}.
         *
         * Default implementation does a nonnull check: {@code instance != null};
         *
         * @param instance The instance to check.
         * @return True when for type T a valid object is present, false when not.
         */
        protected boolean isInstancePresent(T instance)
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
        protected boolean didInsertGetModified(T toInsert, T remainingToInsert)
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
        public IInteractableOperationResult<T> extract(final int slot, final int amount) {
            final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);
            return this.internalTransactionHandlers.get(targets.getFirst()).extract(targets.getSecond(), amount);
        }

        /**
         * The interactable which is being manipulated, once {@link #commit()} is called.
         *
         * @return The interactable.
         */
        @Override
        public final IModifiableInteractable<T> getInteractable() {
            return readWriteInteractable;
        }
    }
}
