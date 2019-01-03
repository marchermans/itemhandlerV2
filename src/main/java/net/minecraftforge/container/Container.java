package net.minecraftforge.container;

import net.minecraftforge.container.api.IContainer;
import net.minecraftforge.util.ListWithFixedSize;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An abstract implementation of the {@link IContainer} interface.
 * Uses a {@link ListWithFixedSize} as backing storage.
 *
 * @param <T> The instance stored in this container.
 */
public class Container<T> implements IContainer<T> {

    /**
     * The underlying datastorage for the container.
     */
    protected List<T> container;

    /**
     * Creates a new container with a given size.
     *
     * @param size The size of the container.
     */
    public Container(final int size)
    {
        this.container = new ListWithFixedSize<>(size);
    }

    /**
     * Creates a new container from the given elements.
     * The container will get the size of the given array.
     *
     * @param container The container.
     */
    public Container(final T... container) {
        this.container = new ListWithFixedSize<>(container);
    }

    /**
     * Creates a new container from the given collection of elements.
     * The container will get the size of the given list.
     *
     * @param iterable The collection to base this container of.
     */
    public Container(final Collection<T> iterable) {
        this.container = new ListWithFixedSize<>(iterable);
    }

    @Override
    public int size() {
        return container.size();
    }

    @Override
    public T get(final int slot) {
        return container.get(slot);
    }
}
