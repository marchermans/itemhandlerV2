package net.minecraftforge.container.api;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ContainerIterator<T> implements Iterator<T> {

    private final IReadOnlyContainer<T> container;

    int index;

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
