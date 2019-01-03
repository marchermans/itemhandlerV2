package net.minecraftforge.interactable.api;

/**
 * A transaction supporting read and write version of the {@link IInteractable}.
 *
 * @param <T> The type stored in the interactable.
 *
 * @see IInteractableTransaction
 */
public interface IModifiableInteractable<T> extends IInteractable<T>
{

    /**
     * Begins a new transaction for this interactable.
     *
     * Either call {@link IInteractableTransaction#commit()}
     * to commit the transaction and make it live, or call {@link IInteractableTransaction#cancel()}
     * to cancel the transaction.
     *
     * @return The transaction object to handle the transaction.
     */
    IInteractableTransaction<T> beginTransaction();

    /**
     * Checks if the given transaction is the active one.
     *
     * @param transactionToCheck The transaction to check.
     * @return True when the given transaction is active and can be commited, false when not.
     */
    boolean isActiveTransaction(final IInteractableTransaction<T> transactionToCheck);
}
