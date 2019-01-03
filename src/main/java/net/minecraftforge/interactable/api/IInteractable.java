package net.minecraftforge.interactable.api;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A generic readonly container for objects in the Minecraft universe.
 * For ItemStacks this is an IItemHandler, for Fluids this is an IFluidHandler and so forth.
 *
 * @param <T> The type contained in this container.
 */
public interface IInteractable<T> extends Iterable<T>
{

    /**
     * Returns the size of the container.
     *
     * EG:
     *   * 27 for a normal small chest.
     *   * 1 for the input of a furnace
     *   * 1 for a tank that can store a single liquid.
     *
     * @return The size of the container.
     */
    int size();

    /**
     * Method to get the contents of the current slot.

     * IMPORTANT: This object MUST NOT be modified. This method is not for
     * altering a interactables contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     *
     * SERIOUSLY: DO NOT MODIFY THE RETURNED OBJECT!
     *
     * @param slot The slot to get the contents from.
     * @return A cloned instance of the object in the slot, or null.
     */
    T get(final int slot);

    /**
     * An immutable representation of this inventory.
     * Might contain nullables if T allows it.

     * IMPORTANT: The objects contained in the list MUST NOT be modified. This method is not for
     * altering a interactables contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     *
     * SERIOUSLY: DO NOT MODIFY THE CONTAINED OBJECTS!
     *
     *
     * @return
     */
    ImmutableList<T> all();

    @Override
    default Iterator<T> iterator()
    {
        return all().iterator();
    }

    @Override
    default Spliterator<T> spliterator()
    {
        return all().spliterator();
    }

    /**
     * Allows for the finding of the first slot index that matches the given predicate.
     *
     * @param checkPredicate The predicate to check with.
     * @return An OptionalInt, possibly containing the first matching slot, or is empty if no slot is found.
     */
    default OptionalInt findFirstSlotMatching(@Nonnull final IInteractableSearchHandler<T> checkPredicate)
    {
        return IntStream.range(0, size())
                .filter(slotIndex -> checkPredicate.test(get(slotIndex)))
                .findFirst();
    }

    /**
     * Method to get a stream of contents of this container.
     * The contents are returned in slot order. Depending on the implementation
     * certain entries might be null.
     *
     * IMPORTANT: The objects in the stream MUST NOT be modified. This method is not for
     * altering a interactables contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     *
     * SERIOUSLY: DO NOT MODIFY THE OBJECTS IN THE STREAM!
     *
     * @return A stream with the contents of this container.
     */
    default Stream<T> stream()
    {
        return all().stream();
    }

    /**
     * Method to get a parallel stream of contents of this container.
     * The contents are returned in slot order. Depending on the implementation
     * certain entries might be null.
     *
     * IMPORTANT: The objects in the stream MUST NOT be modified. This method is not for
     * altering a interactables contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     *
     * SERIOUSLY: DO NOT MODIFY THE OBJECTS IN THE STREAM!
     *
     * @return A stream with the contents of this container.
     */
    default Stream<T> parallelStream()
    {
        return all().parallelStream();
    }
}
