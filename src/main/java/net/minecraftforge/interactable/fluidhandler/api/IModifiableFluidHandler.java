package net.minecraftforge.interactable.fluidhandler.api;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.interactable.api.IModifiableSlottedInteractable;

/**
 * The default read write interactable interface for FluidStacks.
 *
 * @see IModifiableSlottedInteractable
 * @see FluidStack
 */
public interface IModifiableFluidHandler extends IModifiableSlottedInteractable<FluidStack, IFluidHandlerTransaction>
{
}
