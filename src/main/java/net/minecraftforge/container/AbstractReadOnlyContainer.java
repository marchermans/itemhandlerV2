package net.minecraftforge.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.api.IReadOnlyContainer;

/**
 * An abstract implementation of the {@link IReadOnlyContainer} interface.
 * Uses an Array as backing storage.
 *
 * Only missing requirement is an implementation of {@link #makeImmutable(Object)}
 * This is to make sure that this container is actually immutable, since this operation
 * entirely depends on the type T it can not be performed by this class.
 *
 * @param <T> The instance stored in this container.
 */
public abstract class AbstractReadOnlyContainer<T> implements IReadOnlyContainer<T> {

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
    public AbstractReadOnlyContainer(final int size)
    {
        this.container = new Object[size];
    }

    /**
     * Creates a new container from the given elements.
     * The container will get the size of the given array.
     *
     * @param container The container.
     */
    public AbstractReadOnlyContainer(final T ... container) {
        this.container = container;
    }

    @Override
    public int getContainerSize() {
        return container.length;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getContentsOfSlot(final int slot) {
        return makeImmutable((T) container[slot]);
    }

    /**
     * Method called by {@link #getContentsOfSlot(int)} to make the returned value immutable.
     * This can be achieved by creating a deep copy, or in the case of ItemStacks calling {@link ItemStack#copy()}
     *
     * @param tInstance The instance to make immutable.
     *
     * @return A instance of T that ensures that the given instance can not be mutated, even if the returned instance is.
     */
    protected abstract T makeImmutable(final T tInstance);
}
