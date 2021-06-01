package net.minecraftforge.interactable.observer;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.interactable.api.IInteractable;
import net.minecraftforge.interactable.api.IModifiableSlottedInteractable;
import net.minecraftforge.interactable.api.observer.IObserverWatchDog;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ObserverWatchdog<E, I extends IInteractable<E>> implements IObserverWatchDog<E, I>
{

    private final I interactable;
    private final ResourceLocation id;

    public ObserverWatchdog(I interactable, ResourceLocation id) {
        this.interactable = interactable;
        this.id = id;
    }

    @Override
    public I getInteractable() {
        return interactable;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
}
