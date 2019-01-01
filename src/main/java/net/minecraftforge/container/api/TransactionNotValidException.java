package net.minecraftforge.container.api;

public class TransactionNotValidException extends Exception {

    //Exceptions can not be generic......
    private final IReadWriteContainer<?> container;
    private final IContainerTransaction<?> failedTransaction;

    public TransactionNotValidException(final IReadWriteContainer<?> container, final IContainerTransaction<?> failedTransaction) {
        super(String.format("Failed to commit transaction%s to container %s. It is not the active transaction.", failedTransaction, container));
        this.container = container;
        this.failedTransaction = failedTransaction;
    }

    /**
     * The container for which the transaction failed.
     *
     * @return The container for which the transaction failed.
     */
    public IReadWriteContainer<?> getContainer() {
        return container;
    }

    /**
     * The transaction that failed.
     *
     * @return The failed transaction.
     */
    public IContainerTransaction<?> getFailedTransaction() {
        return failedTransaction;
    }
}
