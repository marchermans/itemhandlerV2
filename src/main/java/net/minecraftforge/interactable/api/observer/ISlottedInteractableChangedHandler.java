package net.minecraftforge.interactable.api.observer;

import net.minecraftforge.interactable.api.ISlottedInteractable;

import java.util.Set;

public interface ISlottedInteractableChangedHandler<E>
{
    /**
     * The change handler invoked when then interactable that is given is changed.
     *
     * @param interactable The interactable that has changed.
     * @param changedSlots The slots that changed in the interactable.
     */
    void onChanged(final ISlottedInteractable<E> interactable, final Set<Integer> changedSlots);
}
