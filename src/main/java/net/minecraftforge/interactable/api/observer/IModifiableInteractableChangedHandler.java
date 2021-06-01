package net.minecraftforge.interactable.api.observer;

import net.minecraftforge.interactable.api.IInteractable;
import net.minecraftforge.interactable.api.IInteractableTransaction;
import net.minecraftforge.interactable.api.IModifiableInteractable;

/**
 * Handler invoked as a callback when an interactable is changed
 * and this information is passed on into an observable.
 */
@FunctionalInterface
public interface IModifiableInteractableChangedHandler<E, T extends IInteractableTransaction<E, T>>
{

    /**
     * The change handler invoked when then interactable that is given is changed.
     *
     * @param interactable The interactable that has changed.
     */
    void onChanged(final IModifiableInteractable<E, T> interactable);
}
