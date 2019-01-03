package net.minecraftforge.interactable.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.interactable.api.observer.IObserverWatchDog;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A transaction supporting read and write version of the {@link IInteractable}.
 *
 * @param <T> The type stored in the interactable.
 *
 * @see IInteractableTransaction
 */
public interface IModifiableInteractable<T> extends IInteractable<T>
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
    IInteractableTransaction<T> beginTransaction();

    /**
     * Checks if the given transaction is the active one.
     *
     * @param transactionToCheck The transaction to check.
     * @return True when the given transaction is active and can be commited, false when not.
     */
    boolean isActiveTransaction(final IInteractableTransaction<T> transactionToCheck);

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
    IObserverWatchDog<T> openObserver(final ResourceLocation id, BiConsumer<IModifiableInteractable<T>, Set<Integer>> callback) throws IllegalArgumentException;

    /**
     * Allows for the deregistration of an observer via its watch dog.
     *
     * @param watchDog The watchdog of the observer to deregister.
     * @throws IllegalArgumentException when the id stored in the watchdog is unknown.
     */
    default void closeObserver(final IObserverWatchDog<T> watchDog)
    {
        this.closeObserver(watchDog.getId());
    }

    /**
     * Allows for the deregistration of an observer via its id.
     *
     * @param id The id to deregister.
     * @throws IllegalArgumentException when the id is unknown.
     */
    void closeObserver(final ResourceLocation id) throws IllegalArgumentException;
}
