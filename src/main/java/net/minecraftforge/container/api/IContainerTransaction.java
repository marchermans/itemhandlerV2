package net.minecraftforge.container.api;

import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Represents a single atomic operation of, possibly chained< inserts and extracts
 * that modify the {@link IModifiableContainer} by which the instance was created.
 *
 * @param <T> The type stored in the created {@link IModifiableContainer}.
 */
public interface IContainerTransaction<T> extends IContainer<T> {

    /**
     * Cancels the current transaction.
     */
    void cancel();

    /**
     * Attempts to commit the transaction.
     * If this method is called on a not active transaction, indicated by {@link IModifiableContainer#isActiveTransaction(IContainerTransaction)}
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
     * @return An instance of {@link IContainerOperationResult} that indicates success or failure, and provides results.
     */
    IContainerOperationResult<T> insert(int slot, T toInsert);

    /**
     * Finds a slot who's contents match the predicate first.
     * Then performs an {@link #insert(int, Object)} with that slot and the given instance to insert.
     *
     * @param matchingPredicate Predicate used to find the first matching slot.
     * @param toInsert The instance to insert.
     *
     * @return The result of {{@link #insert(int, Object)}} for the first matching slot.
     */
    default IContainerOperationResult<T> insertIntoFirstMatching(Predicate<T> matchingPredicate, T toInsert)
    {
        final OptionalInt optionalSlotIndex = IntStream.range(0, size())
                .filter(slotIndex -> matchingPredicate.test(get(slotIndex)))
                .findFirst();

        if (optionalSlotIndex.isPresent())
            return insert(optionalSlotIndex.getAsInt(), toInsert);

        return ContainerOperationResult.failed();
    }

    /**
     * Attempts to insert the given instance into this container. Splitting if required.
     *
     * @param toInsert The instance to insert.
     *
     * @return An instance of {@link IContainerOperationResult} that indicates success or failure, and provides results.
     */
    IContainerOperationResult<T> insert(T toInsert);

    /**
     * Attempts to extract a given amount from the slot of this container.
     *
     * @param slot The slot to extract from.
     * @param amount The amount to extract.
     *
     * @return An instance of {@link IContainerOperationResult} that indicates success or failure, and provides results.
     */
    IContainerOperationResult<T> extract(int slot, int amount);

    /**
     * The container which is being manipulated, once {@link #commit()} is called.
     *
     * @return The container.
     */
    IModifiableContainer<T> getContainer();
}
