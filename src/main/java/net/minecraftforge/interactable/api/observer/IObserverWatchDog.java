package net.minecraftforge.interactable.api.observer;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.interactable.api.IModifiableInteractable;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @param <T>
 */
public interface IObserverWatchDog<T> extends AutoCloseable {

    IModifiableInteractable<T> getInteractable();

    ResourceLocation getId();

    BiConsumer<IModifiableInteractable<T>, Set<Integer>> getCallback();

    @Override
    default void close()
    {
        getInteractable().closeObserver(this);
    }
}
