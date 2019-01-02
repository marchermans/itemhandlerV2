package net.minecraftforge.fluidhandler;

import net.minecraftforge.container.Container;
import net.minecraftforge.fluidhandler.api.IFluidHandler;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

public class FluidHandler extends Container<FluidStack> implements IFluidHandler {

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
