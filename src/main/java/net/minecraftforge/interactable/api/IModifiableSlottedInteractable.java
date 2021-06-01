package net.minecraftforge.interactable.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.interactable.api.observer.*;

/**
 * A transaction supporting read and write version of the {@link ISlottedInteractable}.
 *
 * @param <E> The type stored in the interactable.
 * @param <T> The type of the transaction.
 *
 * @see ISlottedInteractableTransaction
 */
public interface IModifiableSlottedInteractable<E, T extends ISlottedInteractableTransaction<E, T>> extends IModifiableInteractable<E, T>, ISlottedInteractable<E>
{
    /**
     * Allows for the registration of a observer for modifiable slotted interactables.
     * Gets called every time the interactables contents change (EG a transaction is committed),
     * and immediately after registration to communicate the initial state.
     *
     * @param id The id of the callback, used to remove the callback again when not needed anymore.
     * @param callback The callback to register.
     *
     * @return A watchdog that can be used as an AutoClosable or used to stop watching.
     * @throws IllegalArgumentException When an observer with the same id is already registered.
     */
    IObserverWatchDog<E, ? extends IModifiableSlottedInteractable<E, T>> openObserver(final ResourceLocation id, IModifiableSlottedInteractableChangedHandler<E, T> callback) throws IllegalArgumentException;

    @Override
    default IObserverWatchDog<E, ? extends ISlottedInteractable<E>> openObserver(
      final ResourceLocation id, ISlottedInteractableChangedHandler<E> callback) throws IllegalArgumentException
    {
        return this.openObserver(
          id,
          (IModifiableSlottedInteractableChangedHandler<E, T>) callback::onChanged
        );
    }

    @Override
    default IObserverWatchDog<E, ? extends IModifiableInteractable<E, T>> openObserver(
      ResourceLocation id, IModifiableInteractableChangedHandler<E, T> callback) throws IllegalArgumentException
    {
        return this.openObserver(
          id,
          (IModifiableSlottedInteractableChangedHandler<E, T>) (interactable, changedSlots) -> callback.onChanged(interactable)
        );
    }

    @Override
    default IObserverWatchDog<E, ? extends IInteractable<E>> openObserver(ResourceLocation id, IInteractableChangedHandler<E> callback) throws IllegalArgumentException
    {
        return this.openObserver(
          id,
          (IModifiableSlottedInteractableChangedHandler<E, T>) (interactable, changedSlots) -> callback.onChanged(interactable)
        );
    }
}
