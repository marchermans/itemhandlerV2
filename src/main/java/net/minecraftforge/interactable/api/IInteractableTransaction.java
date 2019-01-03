package net.minecraftforge.interactable.api;

import javax.annotation.Nonnull;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Represents a single atomic operation of, possibly chained< inserts and extracts
 * that modify the {@link IModifiableInteractable} by which the instance was created.
 *
 * @param <T> The type stored in the created {@link IModifiableInteractable}.
 */
public interface IInteractableTransaction<T> extends IInteractable<T> {

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
     * Attempts to insert the given instance into the slot of this interactable.
     *
     * @param slot The slot to insert into.
     * @param toInsert The object to insert.
     *
     * @return An instance of {@link IInteractableOperationResult} that indicates success or failure, and provides results.
     */
    IInteractableOperationResult<T> insert(int slot, T toInsert);

    /**
     * Finds a slot who's contents match the predicate first.
     * Then performs an {@link #insert(int, Object)} with that slot and the given instance to insert.
     *
     * @param matchingPredicate Predicate used to find the first matching slot.
     * @param toInsert The instance to insert.
     *
     * @return The result of {{@link #insert(int, Object)}} for the first matching slot.
     */
    default IInteractableOperationResult<T> insertIntoFirstMatching(Predicate<T> matchingPredicate, T toInsert)
    {
        final OptionalInt optionalSlotIndex = findFirstSlotMatching(matchingPredicate);

        if (optionalSlotIndex.isPresent())
            return insert(optionalSlotIndex.getAsInt(), toInsert);

        return InteractableOperationResult.failed();
    }

    /**
     * Attempts to insert the given instance into this interactable. Splitting if required.
     *
     * @param toInsert The instance to insert.
     *
     * @return An instance of {@link IInteractableOperationResult} that indicates success or failure, and provides results.
     */
    IInteractableOperationResult<T> insert(T toInsert);

    /**
     * Attempts to extract a given amount from the slot of this interactable.
     *
     * @param slot The slot to extract from.
     * @param amount The amount to extract.
     *
     * @return An instance of {@link IInteractableOperationResult} that indicates success or failure, and provides results.
     */
    IInteractableOperationResult<T> extract(int slot, int amount);

    /**
     * Allows for extraction of the first entry that matches the predicate.
     * Extracts as much as possible.
     *
     * Default implementation calls: {@code extractFirstMatching(checkPredicate, Integer.MAX_VALUE);}
     *
     * @param checkPredicate The predicate to find the first slot matching with.
     * @return An instance of {@link IInteractableOperationResult} that indicates success or failure, and provides results.
     */
    default IInteractableOperationResult<T> extractFirstMatching(Predicate<T> checkPredicate)
    {
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
    default IInteractableOperationResult<T> extractFirstMatching(Predicate<T> checkPredicate, int amount)
    {
        final OptionalInt optionalSlotIndex = findFirstSlotMatching(checkPredicate);

        if (optionalSlotIndex.isPresent())
            return extract(optionalSlotIndex.getAsInt(), amount);

        return InteractableOperationResult.failed();
    }

    /**
     * The interactable which is being manipulated, once {@link #commit()} is called.
     *
     * @return The interactable.
     */
    IModifiableInteractable<T> getInteractable();
}
