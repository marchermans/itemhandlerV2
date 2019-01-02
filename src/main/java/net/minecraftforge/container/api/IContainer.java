package net.minecraftforge.container.api;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A generic readonly container for objects in the Minecraft universe.
 * For ItemStacks this is an IItemHandler, for Fluids this is an IFluidHandler and so forth.
 *
 * @param <T> The type contained in this container.
 */
public interface IContainer<T> extends Iterable<T>
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
    int getSize();

    /**
     * Method to get the contents of the current slot.
     *
     * <p/>
     * IMPORTANT: This object MUST NOT be modified. This method is not for
     * altering a containers contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * <p/>
     * SERIOUSLY: DO NOT MODIFY THE RETURNED OBJECT!
     *
     * @param slot The slot to get the contents from.
     * @return A cloned instance of the object in the slot, or null.
     */
    T get(final int slot);

    @Override
    default Iterator<T> iterator()
    {
        return new ContainerIterator<>(this);
    }

    @Override
    default Spliterator<T> spliterator()
    {
        return Spliterators.spliterator(iterator(), getSize(), Spliterator.SIZED | Spliterator.IMMUTABLE | Spliterator.ORDERED);
    }

    /**
     * Method to get a stream of contents of this container.
     * The contents are returned in slot order. Depending on the implementation
     * certain entries might be null.
     *
     * <p/>
     * IMPORTANT: The objects in the stream MUST NOT be modified. This method is not for
     * altering a containers contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * <p/>
     * SERIOUSLY: DO NOT MODIFY THE OBJECTS IN THE STREAM!
     *
     * @return A stream with the contents of this container.
     */
    default Stream<T> stream()
    {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Method to get a parallel stream of contents of this container.
     * The contents are returned in slot order. Depending on the implementation
     * certain entries might be null.
     *
     * <p/>
     * IMPORTANT: The objects in the stream MUST NOT be modified. This method is not for
     * altering a containers contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * <p/>
     * SERIOUSLY: DO NOT MODIFY THE OBJECTS IN THE STREAM!
     *
     * @return A stream with the contents of this container.
     */
    default Stream<T> parallelStream()
    {
        return StreamSupport.stream(spliterator(), true);
    }
}
