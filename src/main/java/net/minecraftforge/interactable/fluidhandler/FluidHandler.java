package net.minecraftforge.interactable.fluidhandler;

import net.minecraftforge.interactable.SlottedInteractable;
import net.minecraftforge.interactable.fluidhandler.api.IFluidHandler;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

public class FluidHandler extends SlottedInteractable<FluidStack> implements IFluidHandler {

    public FluidHandler(int size) {
        super(size);
    }

    public FluidHandler(FluidStack... iterable) {
        super(iterable);
    }

    public FluidHandler(Collection<FluidStack> iterable)
    {
        super(iterable);
    }
}
