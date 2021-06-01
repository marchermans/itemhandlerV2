package net.minecraftforge.interactable.api;

import java.util.OptionalInt;

/**
 * Represents a single atomic operation of, possibly chained< inserts and extracts
 * that modify the {@link IModifiableSlottedInteractable} by which the instance was created.
 *
 * @param <E> The type stored in the created {@link IModifiableSlottedInteractable}.
 */
public interface ISlottedInteractableTransaction<E, T extends ISlottedInteractableTransaction<E, T>> extends ISlottedInteractable<E>, IInteractableTransaction<E, T>
{

    /**
     * Attempts to insert the given instance into the slot of this interactable.
     *
     * @param slot The slot to insert into.
     * @param toInsert The object to insert.
     *
     * @return An instance of {@link IInteractableOperationResult} that indicates success or failure, and provides results.
     */
    IInteractableOperationResult<E> insert(int slot, E toInsert);

    /**
     * Finds a slot who's contents match the predicate first.
     * Then performs an {@link #insert(int, Object)} with that slot and the given instance to insert.
     *
     * @param matchingPredicate Predicate used to find the first matching slot.
     * @param toInsert The instance to insert.
     *
     * @return The result of {{@link #insert(int, Object)}} for the first matching slot.
     */
    default IInteractableOperationResult<E> insertIntoFirstMatching(IInteractableSearchHandler<E> matchingPredicate, E toInsert)
    {
        final OptionalInt optionalSlotIndex = findFirstSlotMatching(matchingPredicate);

        if (optionalSlotIndex.isPresent())
            return insert(optionalSlotIndex.getAsInt(), toInsert);

        return InteractableOperationResult.failed();
    }

    /**
     * Attempts to extract a given amount from the slot of this interactable.
     *
     * @param slot The slot to extract from.
     * @param amount The amount to extract.
     *
     * @return An instance of {@link IInteractableOperationResult} that indicates success or failure, and provides results.
     */
    IInteractableOperationResult<E> extract(int slot, int amount);

    @Override
    default IInteractableOperationResult<E> extractFirstMatching(IInteractableSearchHandler<E> checkPredicate)
    {
        return extractFirstMatching(checkPredicate, Integer.MAX_VALUE);
    }

    @Override
    default IInteractableOperationResult<E> extractFirstMatching(IInteractableSearchHandler<E> checkPredicate, int amount)
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
    IModifiableSlottedInteractable<E, T> getInteractable();
}
