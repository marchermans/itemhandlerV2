package net.minecraftforge.container.api;

import java.util.*;

public interface IReadOnlyContainer<T> extends Iterable<T>
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
    int getContainerSize();

    /**
     * Method to get the contents of the current slot.
     *
     * The returned object is a cloned instance of the object in the container
     * as such the returned object can be modified without modifying the
     * contents of this container.
     *
     * @param slot The slot to get the contents from.
     * @return A cloned instance of the object in the slot, or null.
     */
    T getContentsOfSlot(final int slot);

    @Override
    default Iterator<T> iterator()
    {
        return new ContainerIterator<>(this);
    }

    @Override
    default Spliterator<T> spliterator()
    {
        return Spliterators.spliterator(iterator(), getContainerSize(), Spliterator.SIZED | Spliterator.IMMUTABLE);
    }


}
