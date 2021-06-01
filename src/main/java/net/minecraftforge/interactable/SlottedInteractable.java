package net.minecraftforge.interactable;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.interactable.api.ISlottedInteractable;
import net.minecraftforge.interactable.api.observer.IObserverWatchDog;
import net.minecraftforge.interactable.api.observer.ISlottedInteractableChangedHandler;
import net.minecraftforge.interactable.observer.ObserverWatchdog;
import net.minecraftforge.util.ListWithFixedSize;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An abstract implementation of the {@link ISlottedInteractable} interface.
 * Uses a {@link ListWithFixedSize} as backing storage.
 *
 * @param <E> The instance stored in this interactable.
 */
public class SlottedInteractable<E> implements ISlottedInteractable<E>
{

    private final Map<ResourceLocation, ISlottedInteractableChangedHandler<E>> observers = new HashMap<>();

    /**
     * The underlying datastorage for the interactable.
     */
    protected     List<E>                                                      interactable;

    /**
     * Creates a new interactable with a given size.
     *
     * @param size The size of the interactable.
     */
    public SlottedInteractable(final int size)
    {
        this.interactable = new ListWithFixedSize<>(size);
    }

    /**
     * Creates a new interactable from the given elements.
     * The interactable will get the size of the given array.
     *
     * @param interactable The interactable.
     */
    public SlottedInteractable(final E... interactable) {
        this.interactable = new ListWithFixedSize<>(interactable);
    }

    /**
     * Creates a new interactable from the given collection of elements.
     * The interactable will get the size of the given list.
     *
     * @param iterable The collection to base this interactable of.
     */
    public SlottedInteractable(final Collection<E> iterable) {
        this.interactable = new ListWithFixedSize<>(iterable);
    }

    @Override
    public int size() {
        return interactable.size();
    }

    @Override
    public E get(final int slot) {
        return interactable.get(slot);
    }

    @Override
    public ImmutableList<E> all() {
        return ImmutableList.copyOf(interactable);
    }

    @Override
    public void closeObserver(final ResourceLocation id) throws IllegalArgumentException
    {
        if (!observers.containsKey(id))
            throw new IllegalArgumentException(String.format("Observer with ID: %s is not registered", id));

        observers.remove(id);
    }

    @Override
    public IObserverWatchDog<E, ? extends ISlottedInteractable<E>> openObserver(
      final ResourceLocation id, final ISlottedInteractableChangedHandler<E> callback) throws IllegalArgumentException
    {
        if (observers.containsKey(id))
            throw new IllegalArgumentException(String.format("Observer with ID: %s is already registered.", id));

        observers.put(id, callback);

        return new ObserverWatchdog<>(this, id);
    }
}
