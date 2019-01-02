package net.minecraftforge.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.api.IReadOnlyContainer;

/**
 * An abstract implementation of the {@link IReadOnlyContainer} interface.
 * Uses an Array as backing storage.
 *
 * @param <T> The instance stored in this container.
 */
public class ReadOnlyContainer<T> implements IReadOnlyContainer<T> {

    /**
     * The underlying datastorage for the container.
     * Since {@code T[]} is not a valid java declaration we are using an Object[] here.
     */
    protected Object[] container;

    /**
     * Creates a new container with a given size.
     *
     * @param size The size of the container.
     */
    public ReadOnlyContainer(final int size)
    {
        this.container = new Object[size];
    }

    /**
     * Creates a new container from the given elements.
     * The container will get the size of the given array.
     *
     * @param container The container.
     */
    protected ReadOnlyContainer(final Object ... container) {
        this.container = container;
    }

    @Override
    public int getContainerSize() {
        return container.length;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getContentsOfSlot(final int slot) {
        return (T) container[slot];
    }
}
