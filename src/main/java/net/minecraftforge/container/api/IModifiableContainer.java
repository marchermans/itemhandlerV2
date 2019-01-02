package net.minecraftforge.container.api;

/**
 * A transaction supporting read and write version of the {@link IContainer}.
 *
 * @param <T> The type stored in the container.
 *
 * @see IContainerTransaction
 */
public interface IModifiableContainer<T> extends IContainer<T>
{

    /**
     * Begins a new transaction for this container.
     *
     * Either call {@link IContainerTransaction#commit()}
     * to commit the transaction and make it live, or call {@link IContainerTransaction#cancel()}
     * to cancel the transaction.
     *
     * @return The transaction object to handle the transaction.
     */
    IContainerTransaction<T> beginTransaction();

    /**
     * Checks if the given transaction is the active one.
     *
     * @param transactionToCheck The transaction to check.
     * @return True when the given transaction is active and can be commited, false when not.
     */
    boolean isActiveTransaction(final IContainerTransaction<T> transactionToCheck);
}
