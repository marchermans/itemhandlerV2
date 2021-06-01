package net.minecraftforge.interactable.api.observer;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.interactable.api.IInteractable;
import net.minecraftforge.interactable.api.IInteractableTransaction;
import net.minecraftforge.interactable.api.IModifiableInteractable;
import net.minecraftforge.interactable.api.IModifiableSlottedInteractable;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Defines an auto closeable link of an observer callback to an {@link IInteractable}
 *
 * @param <E> The type contained in the interactable being watched.
 * @param <I> The type of the interactable being watched.
 */
public interface IObserverWatchDog<E, I extends IInteractable<E>> extends AutoCloseable {

    /**
     * The interactable by which this watchdog was created.
     *
     * @return The interactable.
     */
    I getInteractable();

    /**
     * The id of the observer.
     *
     * @return The id.
     */
    ResourceLocation getId();

    @Override
    default void close()
    {
        getInteractable().closeObserver(this);
    }
}
