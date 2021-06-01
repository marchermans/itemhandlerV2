package net.minecraftforge.interactable.energyhandler.api;

import net.minecraftforge.interactable.api.ISlottedInteractable;
import net.minecraftforge.interactable.api.IModifiableSlottedInteractable;

/**
 * The default read write interactable interface for Forge Energy.
 *
 * @see ISlottedInteractable
 */
public interface IModifiableForgeEnergyHandler extends IModifiableSlottedInteractable<Integer, IForgeEnergyHandlerTransaction>, IForgeEnergyHandler {
}
