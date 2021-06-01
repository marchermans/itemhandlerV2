package net.minecraftforge.interactable.energyhandler.api;

import net.minecraftforge.interactable.api.ISlottedInteractableTransaction;

/**
 * The default interactable transaction interface for ForgeEnergy.
 *
 * @see ISlottedInteractableTransaction
 */
public interface IForgeEnergyHandlerTransaction extends ISlottedInteractableTransaction<Integer, IForgeEnergyHandlerTransaction>, IForgeEnergyHandler {
}
