package net.minecraftforge.energyhandler;

import net.minecraftforge.container.Container;
import net.minecraftforge.energyhandler.api.IEnergyHandler;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collection;

public class EnergyHandler extends Container<Integer> implements IEnergyHandler {

    public EnergyHandler(int size) {
        super(size);
    }

    public EnergyHandler(Integer... iterable) {
        super(iterable);
    }

    public EnergyHandler(Collection<Integer> iterable)
    {
        super(iterable);
    }
}
