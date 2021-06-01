package net.minecraftforge.interactable.api.observer;

import net.minecraftforge.interactable.api.IModifiableSlottedInteractable;
import net.minecraftforge.interactable.api.ISlottedInteractable;
import net.minecraftforge.interactable.api.ISlottedInteractableTransaction;

import java.util.Set;

public interface IModifiableSlottedInteractableChangedHandler<E, T extends ISlottedInteractableTransaction<E, T>>
{
    /**
     * The change handler invoked when then interactable that is given is changed.
     *
     * @param interactable The interactable that has changed.
     * @param changedSlots The slots that changed in the interactable.
     */
    void onChanged(final IModifiableSlottedInteractable<E, T> interactable, final Set<Integer> changedSlots);
}
