package net.minecraftforge.interactable.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.interactable.api.observer.IInteractableChangedHandler;
import net.minecraftforge.interactable.api.observer.IObserverWatchDog;
import net.minecraftforge.interactable.api.observer.ISlottedInteractableChangedHandler;

import javax.annotation.Nonnull;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * A generic readonly container for objects in the Minecraft universe which uses slots to differentiate between the objects.
 * For ItemStacks this is an IItemHandler, for Fluids this is an IFluidHandler and so forth.
 *
 * @param <E> The type contained in this container.
 */
public interface ISlottedInteractable<E> extends IInteractable<E>
{
    /**
     * Method to get the contents of the current slot.

     * IMPORTANT: This object MUST NOT be modified. This method is not for
     * altering a interactables contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     *
     * SERIOUSLY: DO NOT MODIFY THE RETURNED OBJECT!
     *
     * @param slot The slot to get the contents from.
     * @return A cloned instance of the object in the slot, or null.
     */
    E get(final int slot);

    /**
     * Allows for the finding of the first slot that matches the given predicate.
     *
     * @param checkPredicate The predicate to check with.
     * @return An OptionalInt, possibly containing the first matching slot, or is empty if no slot is found.
     */
    default OptionalInt findFirstSlotMatching(@Nonnull final IInteractableSearchHandler<E> checkPredicate)
    {
        return IntStream.range(0, size())
                .filter(slotIndex -> checkPredicate.test(get(slotIndex)))
                .findFirst();
    }

    /**
     * Allows for the registration of a observer for slotted interactables.
     * Gets called every time the interactables contents change (EG a transaction is committed),
     * and immediately after registration to communicate the initial state.
     *
     * @param id The id of the callback, used to remove the callback again when not needed anymore.
     * @param callback The callback to register.
     *
     * @return A watchdog that can be used as an AutoClosable or used to stop watching.
     * @throws IllegalArgumentException When an observer with the same id is already registered.
     */
    IObserverWatchDog<E, ? extends ISlottedInteractable<E>> openObserver(final ResourceLocation id, ISlottedInteractableChangedHandler<E> callback) throws IllegalArgumentException;

    @Override
    default IObserverWatchDog<E, ? extends IInteractable<E>> openObserver(ResourceLocation id, IInteractableChangedHandler<E> callback) throws IllegalArgumentException
    {
        return this.openObserver(
          id,
          (interactable, changedSlots) -> callback.onChanged(interactable)
        );
    }

}
