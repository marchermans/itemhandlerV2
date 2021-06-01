package net.minecraftforge.interactable.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.interactable.api.observer.IInteractableChangedHandler;
import net.minecraftforge.interactable.api.observer.IModifiableInteractableChangedHandler;
import net.minecraftforge.interactable.api.observer.IObserverWatchDog;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A transaction supporting read and write version of the {@link IInteractable}.
 *
 * @param <E> The type stored in the interactable.
 * @param <T> The type of the transaction.
 *
 * @see ISlottedInteractableTransaction
 */
public interface IModifiableInteractable<E, T extends IInteractableTransaction<E, T>> extends IInteractable<E>
{
    /**
     * Begins a new transaction for this interactable.
     *
     * Either call {@link IInteractableTransaction#commit()}
     * to commit the transaction and make it live, or call {@link IInteractableTransaction#cancel()}
     * to cancel the transaction.
     *
     * @return The transaction object to handle the transaction.
     */
    T beginTransaction();

    /**
     * Checks if the given transaction is the active one.
     *
     * @param transactionToCheck The transaction to check.
     * @return True when the given transaction is active and can be commited, false when not.
     */
    boolean isActiveTransaction(T transactionToCheck);

    /**
     * Allows for the registration of a observer for modifiable interactables.
     * Gets called every time the interactables contents change (EG a transaction is committed),
     * and immediately after registration to communicate the initial state.
     *
     * @param id The id of the callback, used to remove the callback again when not needed anymore.
     * @param callback The callback to register.
     *
     * @return A watchdog that can be used as an AutoClosable or used to stop watching.
     * @throws IllegalArgumentException When an observer with the same id is already registered.
     */
    IObserverWatchDog<E, ? extends IModifiableInteractable<E, T>> openObserver(ResourceLocation id, IModifiableInteractableChangedHandler<E, T> callback) throws IllegalArgumentException;

    @Override
    default IObserverWatchDog<E, ? extends IInteractable<E>> openObserver(ResourceLocation id, IInteractableChangedHandler<E> callback) throws IllegalArgumentException
    {
        return this.openObserver(id, (IModifiableInteractableChangedHandler<E, T>) callback::onChanged);
    }
}
