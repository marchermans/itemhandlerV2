package net.minecraftforge.container;

import net.minecraft.util.Tuple;
import net.minecraftforge.container.api.*;

import java.util.List;
import java.util.stream.Collectors;

public class CombiningModifiableContainer<T> extends CombiningContainer<T> implements IModifiableContainer<T> {

    private List<IModifiableContainer<T>> readWriteContainers;
    private IContainerTransaction<T> activeTransaction;

    public CombiningModifiableContainer(List<IModifiableContainer<T>> iModifiableContainers) {
        super(iModifiableContainers);
        this.readWriteContainers = iModifiableContainers;
    }

    /**
     * Begins a new transaction for this container.
     * <p>
     * Either call {@link IContainerTransaction#commit()}
     * to commit the transaction and make it live, or call {@link IContainerTransaction#cancel()}
     * to cancel the transaction.
     *
     * @return The transaction object to handle the transaction.
     */
    @Override
    public IContainerTransaction<T> beginTransaction() {
        this.activeTransaction = new CombiningContainerTransaction<>(this);
        return activeTransaction;
    }

    /**
     * Checks if the given transaction is the active one.
     *
     * @param transactionToCheck The transaction to check.
     * @return True when the given transaction is active and can be commited, false when not.
     */
    @Override
    public boolean isActiveTransaction(IContainerTransaction<T> transactionToCheck) {
        return activeTransaction == transactionToCheck;
    }

    public class CombiningContainerTransaction<T> extends CombiningContainer<T> implements IContainerTransaction<T>
    {

        private final CombiningModifiableContainer<T> readWriteContainer;
        private final List<IContainerTransaction<T>> internalTransactionHandlers;

        public CombiningContainerTransaction(final CombiningModifiableContainer<T> readWriteContainer) {
            super(readWriteContainer.readWriteContainers.stream().map(IModifiableContainer::beginTransaction).collect(Collectors.toList()));
            this.readWriteContainer = readWriteContainer;
            this.internalTransactionHandlers = super.readOnlyContainers.stream().map(container -> (IContainerTransaction<T>) container).collect(Collectors.toList());
        }

        /**
         * Cancels the current transaction.
         */
        @Override
        public void cancel() {
            if (readWriteContainer.isActiveTransaction(this))
                readWriteContainer.activeTransaction = null;

            internalTransactionHandlers.forEach(IContainerTransaction::cancel);
        }

        /**
         * Attempts to commit the transaction.
         * If this method is called on a not active transaction, indicated by {@link IModifiableContainer#isActiveTransaction(IContainerTransaction)}
         * being false, then an exception is thrown.
         *
         * @throws TransactionNotValidException When this transaction is not the active transaction.
         */
        @Override
        public void commit() throws TransactionNotValidException {
            if (!readWriteContainer.isActiveTransaction(this))
                throw new TransactionNotValidException(readWriteContainer, this);

            //First verify that each transaction is still active:
            for (int i = 0; i < this.internalTransactionHandlers.size(); i++) {
                if (!readWriteContainer.readWriteContainers.get(i).isActiveTransaction(this.internalTransactionHandlers.get(i)))
                    throw new TransactionNotValidException(readWriteContainer, this);
            }

            for (final IContainerTransaction<T> internalTransactionHandler : internalTransactionHandlers) {
                internalTransactionHandler.commit();
            }
        }

        /**
         * Attempts to insert the given instance into the slot of this container.
         *
         * @param slot     The slot to insert into.
         * @param toInsert The object to insert.
         * @return An instance of {@link IContainerOperationResult} that indicates success or failure, and provides results.
         */
        @Override
        public IContainerOperationResult<T> insert(final int slot, final T toInsert) {
            final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);
            return this.internalTransactionHandlers.get(targets.getFirst()).insert(targets.getSecond(), toInsert);
        }

        @Override
        public IContainerOperationResult<T> insert(final T toInsert) {
            boolean wasConflicted = false;
            T remainderToInsert = toInsert;
            for (final IContainerTransaction<T> internalTransaction :
                    this.internalTransactionHandlers) {
                final IContainerOperationResult<T> interactionResult = internalTransaction.insert(remainderToInsert);
                if (interactionResult.wasSuccessful())
                {
                    remainderToInsert = interactionResult.getPrimary();
                }
                else if (interactionResult.getStatus().isConflicting())
                {
                    wasConflicted = true;
                    if (interactionResult.getPrimary() != null)
                    {
                        remainderToInsert = interactionResult.getPrimary();
                    }
                }

                if (remainderToInsert == null)
                    return ContainerOperationResult.success(null, null);
            }

            if (toInsert.equals(remainderToInsert))
                return ContainerOperationResult.failed();

            if (wasConflicted)
                return ContainerOperationResult.conflicting(remainderToInsert);

            return ContainerOperationResult.success(remainderToInsert, null);
        }

        /**
         * Attempts to extract a given amount from the slot of this container.
         *
         * @param slot   The slot to extract from.
         * @param amount The amount to extract.
         * @return An instance of {@link IContainerOperationResult} that indicates success or failure, and provides results.
         */
        @Override
        public IContainerOperationResult<T> extract(final int slot, final int amount) {
            final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);
            return this.internalTransactionHandlers.get(targets.getFirst()).extract(targets.getSecond(), amount);
        }

        /**
         * The container which is being manipulated, once {@link #commit()} is called.
         *
         * @return The container.
         */
        @Override
        public IModifiableContainer<T> getContainer() {
            return readWriteContainer;
        }
    }
}
