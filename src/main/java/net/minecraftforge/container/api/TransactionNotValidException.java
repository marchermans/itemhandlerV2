package net.minecraftforge.container.api;

/**
 * An exception thrown when the user tries to commit an none active transaction to a container.
 */
public class TransactionNotValidException extends Exception {

    //Exceptions can not be generic......
    private final IModifiableContainer<?> container;
    private final IContainerTransaction<?> failedTransaction;

    /**
     * Creates a new exception for the given container and transaction.
     *
     * @param container The container.
     * @param failedTransaction The transaction.
     */
    public TransactionNotValidException(final IModifiableContainer<?> container, final IContainerTransaction<?> failedTransaction) {
        super(String.format("Failed to commit transaction%s to container %s. It is not the active transaction.", failedTransaction, container));
        this.container = container;
        this.failedTransaction = failedTransaction;
    }

    /**
     * The container for which the transaction failed.
     *
     * @return The container for which the transaction failed.
     */
    public IModifiableContainer<?> getContainer() {
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
