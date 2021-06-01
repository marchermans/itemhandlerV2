package net.minecraftforge.interactable.api;

/**
 * Represents a single transaction on an interactable.
 *
 * @param <E> Content type of the interactable for which this is an interaction.
 */
public interface IInteractableTransaction<E, T extends IInteractableTransaction<E, T>> extends IInteractable<E>
{
    /**
     * Cancels the current transaction.
     */
    void cancel();

    /**
     * Attempts to commit the transaction.
     * If this method is called on a not active transaction, indicated by {@link IModifiableInteractable#isActiveTransaction(IInteractableTransaction)}
     * being false, then an exception is thrown.
     *
     * @throws TransactionNotValidException When this transaction is not the active transaction.
     */
    void commit() throws TransactionNotValidException;

    /**
     * Attempts to insert the given instance into this interactable. Splitting if required.
     *
     * @param toInsert The instance to insert.
     *
     * @return An instance of {@link IInteractableOperationResult} that indicates success or failure, and provides results.
     */
    IInteractableOperationResult<E> insert(E toInsert);

    /**
     * Allows for extraction of the first entry that matches the predicate.
     * Extracts as much as possible.
     *
     * Default implementation calls: {@code extractFirstMatching(checkPredicate, Integer.MAX_VALUE);}
     *
     * @param checkPredicate The predicate to find the first slot matching with.
     * @return An instance of {@link IInteractableOperationResult} that indicates success or failure, and provides results.
     */
    default IInteractableOperationResult<E> extractFirstMatching(IInteractableSearchHandler<E> checkPredicate) {
        return extractFirstMatching(checkPredicate, Integer.MAX_VALUE);
    }

    /**
     * Allows for extraction of the first entry that matches the predicate.
     * Extracts the given amount.
     *
     * @param checkPredicate The predicate to find the first slot matching with.
     * @param amount The amount to extract.
     * @return An instance of {@link IInteractableOperationResult} that indicates success or failure, and provides results.
     */
    IInteractableOperationResult<E> extractFirstMatching(IInteractableSearchHandler<E> checkPredicate, int amount);

    /**
     * Returns the interactable for which this transaction was created.
     *
     * @return The interactable.
     */
    IModifiableInteractable<E, T> getInteractable();
}
