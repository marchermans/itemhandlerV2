package net.minecraftforge.interactable.api.observer;

import net.minecraftforge.interactable.api.IInteractable;

/**
 * Handler invoked as a callback when an interactable is changed
 * and this information is passed on into an observable.
 */
@FunctionalInterface
public interface IInteractableChangedHandler<E>
{

    /**
     * The change handler invoked when then interactable that is given is changed.
     *
     * @param interactable The interactable that has changed.
     */
    void onChanged(final IInteractable<E> interactable);
}
