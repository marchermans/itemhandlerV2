package net.minecraftforge.interactable.api;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A iterator to loop over interactables.
 *
 * @param <T> The type stored in the interactable.
 */
public class InteractableIterator<T> implements Iterator<T> {

    /**
     * The interactable over which is looped.
     */
    private final IInteractable<T> interactable;

    /**
     * The current index of the interactable.
     */
    int index;

    /**
     * Creates a new iterator for a given readonly interactable.
     *
     * @param interactable The interactable to loop over.
     */
    InteractableIterator(final IInteractable<T> interactable) {
        this.interactable = interactable;
    }

    @Override
    public boolean hasNext()
    {
        return index < interactable.size();
    }

    @Override
    public T next()
    {
        if (!hasNext())
            throw new NoSuchElementException("No element left in interactable to iterate over.");

        final T contentsOfSlot = interactable.get(index);
        index++;
        return contentsOfSlot;
    }
}
