package net.minecraftforge.interactable.api.observer;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.interactable.api.IModifiableInteractable;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ObserveableWatchDog<T> implements IObserverWatchDog<T> {

    private final IModifiableInteractable<T> interactable;
    private final ResourceLocation id;
    private final BiConsumer<IModifiableInteractable<T>, Set<Integer>> callback;

    public ObserveableWatchDog(IModifiableInteractable<T> interactable, ResourceLocation id, BiConsumer<IModifiableInteractable<T>, Set<Integer>> callback) {
        this.interactable = interactable;
        this.id = id;
        this.callback = callback;
    }

    @Override
    public IModifiableInteractable<T> getInteractable() {
        return interactable;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public BiConsumer<IModifiableInteractable<T>, Set<Integer>> getCallback() {
        return callback;
    }
}
