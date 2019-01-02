package net.minecraftforge.fluidhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.ReadOnlyContainer;
import net.minecraftforge.fluidhandler.api.IReadOnlyFluidHandler;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;

public class ReadOnlyFluidHandler extends ReadOnlyContainer<FluidStack> implements IReadOnlyFluidHandler {

    public ReadOnlyFluidHandler(int size) {
        super(size);
    }

    public ReadOnlyFluidHandler(FluidStack... iterable) {
        super(iterable);
    }

    public ReadOnlyFluidHandler(Collection<FluidStack> iterable)
    {
        super(iterable);
    }
}
