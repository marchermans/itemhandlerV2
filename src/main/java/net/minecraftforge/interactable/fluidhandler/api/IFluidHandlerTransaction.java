package net.minecraftforge.interactable.fluidhandler.api;

import net.minecraftforge.interactable.api.ISlottedInteractableTransaction;
import net.minecraftforge.fluids.FluidStack;

/**
 * The default interactable transaction interface for FluidStacks.
 *
 * @see ISlottedInteractableTransaction
 * @see FluidStack
 */
public interface IFluidHandlerTransaction extends ISlottedInteractableTransaction<FluidStack, IFluidHandlerTransaction>
{
}
