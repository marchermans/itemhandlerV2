package net.minecraftforge.container.api;

public interface IReadWriteContainer<T> extends IReadOnlyContainer<T>
{

    /**
     * Begins a new transaction for this container.
     *
     * Either call {@link IContainerTransaction#commit()} or {@link #commitTransaction(IContainerTransaction)}
     * to commit the transaction and make it live, or call {@link IContainerTransaction#cancel()}
     * to cancel the transaction.
     *
     * @return The transaction object to handle the transaction.
     */
    IContainerTransaction<T> beginTransaction();

    /**
     * Ends a given transaction and makes its changes the live changes.
     * Can only be called with the latest result of {@link #beginTransaction()}
     *
     * If called with any other instance of {@link IContainerTransaction} then an {@link IllegalArgumentException} is thrown.
     *
     * @param transactionToCommit The transaction to commit.
     * @throws TransactionNotValidException when a not active {@link IContainerTransaction} is given to commit
     */
    void commitTransaction(final IContainerTransaction<T> transactionToCommit) throws TransactionNotValidException;

    /**
     * Checks if the given transaction is the active one.
     *
     * @param transactionToCheck The transaction to check.
     * @return True when the given transaction is active and can be commited, false when not.
     */
    boolean isActiveTransaction(final IContainerTransaction<T> transactionToCheck);
}
