package net.minecraftforge.interactable.api;

/**
 * An exception thrown when the user tries to commit an none active transaction to a interactable.
 */
public class TransactionNotValidException extends Exception {

    //Exceptions can not be generic......
    private final IModifiableInteractable<?, ?>  interactable;
    private final ISlottedInteractableTransaction<?, ?> failedTransaction;

    /**
     * Creates a new exception for the given interactable and transaction.
     *
     * @param interactable The interactable.
     * @param failedTransaction The transaction.
     */
    public TransactionNotValidException(final IModifiableSlottedInteractable<?, ?> interactable, final ISlottedInteractableTransaction<?, ?> failedTransaction) {
        super(String.format("Failed to commit transaction%s to interactable %s. It is not the active transaction.", failedTransaction, interactable));
        this.interactable = interactable;
        this.failedTransaction = failedTransaction;
    }

    /**
     * The interactable for which the transaction failed.
     *
     * @return The interactable for which the transaction failed.
     */
    public IModifiableInteractable<?, ?> getInteractable() {
        return interactable;
    }

    /**
     * The transaction that failed.
     *
     * @return The failed transaction.
     */
    public ISlottedInteractableTransaction<?, ?> getFailedTransaction() {
        return failedTransaction;
    }
}
