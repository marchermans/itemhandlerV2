package net.minecraftforge.container.api;

/**
 * Represents a single atomic operation of, possibly chained< inserts and extracts
 * that modify the {@link IReadWriteContainer} by which the instance was created.
 *
 * @param <T> The type stored in the created {@link IReadWriteContainer}.
 */
public interface IContainerTransaction<T> extends IReadOnlyContainer<T> {

    /**
     * Cancels the current transaction.
     */
    void cancel();

    /**
     * Attempts to commit the transaction.
     * If this method is called on a not active transaction, indicated by {@link IReadWriteContainer#isActiveTransaction(IContainerTransaction)}
     * being false, then an exception is thrown.
     *
     * @throws TransactionNotValidException When this transaction is not the active transaction.
     */
    void commit() throws TransactionNotValidException;

    /**
     * Attempts to insert the given instance into the slot of this container.
     *
     * @param slot The slot to insert into.
     * @param toInsert The object to insert.
     *
     * @return An instance of {@link IContainerTransactionOperationResult} that indicates success or failure, and provides results.
     */
    IContainerTransactionOperationResult<T> insert(int slot, T toInsert);


    /**
     * Attempts to extract a given amount from the slot of this container.
     *
     * @param slot The slot to extract from.
     * @param amount The amount to extract.
     *
     * @return An instance of {@link IContainerTransactionOperationResult} that indicates success or failure, and provides results.
     */
    IContainerTransactionOperationResult<T> extract(int slot, int amount);

    /**
     * The container which is being manipulated, once {@link #commit()} is called.
     *
     * @return The container.
     */
    IReadWriteContainer<T> getContainer();
}
