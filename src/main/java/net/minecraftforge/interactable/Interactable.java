package net.minecraftforge.interactable;

import net.minecraftforge.interactable.api.IInteractable;
import net.minecraftforge.util.ListWithFixedSize;

import java.util.Collection;
import java.util.List;

/**
 * An abstract implementation of the {@link IInteractable} interface.
 * Uses a {@link ListWithFixedSize} as backing storage.
 *
 * @param <T> The instance stored in this interactable.
 */
public class Interactable<T> implements IInteractable<T> {

    /**
     * The underlying datastorage for the interactable.
     */
    protected List<T> interactable;

    /**
     * Creates a new interactable with a given size.
     *
     * @param size The size of the interactable.
     */
    public Interactable(final int size)
    {
        this.interactable = new ListWithFixedSize<>(size);
    }

    /**
     * Creates a new interactable from the given elements.
     * The interactable will get the size of the given array.
     *
     * @param interactable The interactable.
     */
    public Interactable(final T... interactable) {
        this.interactable = new ListWithFixedSize<>(interactable);
    }

    /**
     * Creates a new interactable from the given collection of elements.
     * The interactable will get the size of the given list.
     *
     * @param iterable The collection to base this interactable of.
     */
    public Interactable(final Collection<T> iterable) {
        this.interactable = new ListWithFixedSize<>(iterable);
    }

    @Override
    public int size() {
        return interactable.size();
    }

    @Override
    public T get(final int slot) {
        return interactable.get(slot);
    }
}
