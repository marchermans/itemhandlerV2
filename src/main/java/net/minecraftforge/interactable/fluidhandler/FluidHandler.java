package net.minecraftforge.interactable.fluidhandler;

import net.minecraftforge.interactable.Interactable;
import net.minecraftforge.interactable.fluidhandler.api.IFluidHandler;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

public class FluidHandler extends Interactable<FluidStack> implements IFluidHandler {

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
