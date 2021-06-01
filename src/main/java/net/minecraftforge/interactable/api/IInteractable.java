package net.minecraftforge.interactable.api;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.interactable.api.observer.IInteractableChangedHandler;
import net.minecraftforge.interactable.api.observer.IObserverWatchDog;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Stream;


/**
 * A generic readonly container for objects in the Minecraft universe.
 * For ItemStacks this is an IItemHandler, for Fluids this is an IFluidHandler and so forth.
 *
 * @param <E> The type contained in this container.
 */
public interface IInteractable<E> extends Iterable<E>
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
    default int size()
    {
        return all().size();
    }

    /**
     * An immutable representation of this inventory.
     * Might contain nullables if T allows it.

     * IMPORTANT: The objects contained in the list MUST NOT be modified. This method is not for
     * altering a interactables contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     *
     * SERIOUSLY: DO NOT MODIFY THE CONTAINED OBJECTS!
     *
     * @return An {@link ImmutableList} with the contents of this interactable.
     */
    ImmutableList<E> all();

    @NotNull
    @Override
    default Iterator<E> iterator()
    {
        return all().iterator();
    }

    @NotNull
    @Override
    default Spliterator<E> spliterator()
    {
        return all().spliterator();
    }

    /**
     * Allows for the finding of the first entry that matches the given predicate.
     *
     * @param checkPredicate The predicate to check with.
     * @return An Optional, possibly containing the first matching entry, or is empty if no slot is found.
     */
    @NotNull
    default Optional<E> findFirstMatching(@Nonnull final IInteractableSearchHandler<E> checkPredicate)
    {
        return stream()
                 .filter(checkPredicate)
                 .findFirst();
    }

    /**
     * Method to get a stream of contents of this container.
     * The contents are returned in slot order. Depending on the implementation
     * certain entries might be null.
     *
     * IMPORTANT: The objects in the stream MUST NOT be modified. This method is not for
     * altering a interactables contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     *
     * SERIOUSLY: DO NOT MODIFY THE OBJECTS IN THE STREAM!
     *
     * @return A stream with the contents of this container.
     */
    @NotNull
    default Stream<E> stream()
    {
        return all().stream();
    }

    /**
     * Method to get a parallel stream of contents of this container.
     * The contents are returned in slot order. Depending on the implementation
     * certain entries might be null.
     *
     * IMPORTANT: The objects in the stream MUST NOT be modified. This method is not for
     * altering a interactables contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     *
     * SERIOUSLY: DO NOT MODIFY THE OBJECTS IN THE STREAM!
     *
     * @return A stream with the contents of this container.
     */
    @NotNull
    default Stream<E> parallelStream()
    {
        return all().parallelStream();
    }

    /**
     * Allows for the registration of a observer for interactables.
     * Gets called every time the interactables contents change (EG a transaction is committed),
     * and immediately after registration to communicate the initial state.
     *
     * @param id The id of the callback, used to remove the callback again when not needed anymore.
     * @param callback The callback to register.
     *
     * @return A watchdog that can be used as an AutoClosable or used to stop watching.
     * @throws IllegalArgumentException When an observer with the same id is already registered.
     */
    IObserverWatchDog<E, ? extends IInteractable<E>> openObserver(ResourceLocation id, IInteractableChangedHandler<E> callback) throws IllegalArgumentException;

    /**
     * Allows for the deregistration of an observer via its watch dog.
     *
     * @param watchDog The watchdog of the observer to deregister.
     * @throws IllegalArgumentException when the id stored in the watchdog is unknown.
     */
    default void closeObserver(final IObserverWatchDog<E, ?> watchDog)
    {
        this.closeObserver(watchDog.getId());
    }

    /**
     * Allows for the deregistration of an observer via its id.
     *
     * @param id The id to deregister.
     * @throws IllegalArgumentException when the id is unknown.
     */
    void closeObserver(ResourceLocation id) throws IllegalArgumentException;
}
