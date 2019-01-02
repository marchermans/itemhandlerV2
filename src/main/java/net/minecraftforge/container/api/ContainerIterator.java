package net.minecraftforge.container.api;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A iterator to loop over containers.
 * The iterator ensures that a copy is created of each object before handing it to
 * the caller of {@link #next()}. This ensures that the underlying {@link IReadOnlyContainer}
 * stays immutable.
 *
 * @param <T> The type stored in the container.
 */
public class ContainerIterator<T> implements Iterator<T> {

    /**
     * The container over which is looped.
     */
    private final IReadOnlyContainer<T> container;

    /**
     * The current index of the container.
     */
    int index;

    /**
     * Creates a new iterator for a given readonly container.
     *
     * @param container The container to loop over.
     */
    public ContainerIterator(final IReadOnlyContainer<T> container) {
        this.container = container;
    }

    @Override
    public boolean hasNext()
    {
        return index < container.getContainerSize();
    }

    @Override
    public T next()
    {
        if (!hasNext())
            throw new NoSuchElementException("No element left in container to iterate over.");

        final T contentsOfSlot = container.getContentsOfSlot(index);
        index++;
        return contentsOfSlot;
    }
}
