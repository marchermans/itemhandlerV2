package net.minecraftforge.interactable.energyhandler;

import net.minecraftforge.interactable.Interactable;
import net.minecraftforge.interactable.energyhandler.api.IForgeEnergyHandler;

import java.util.Collection;

public class ForgeEnergyHandler extends Interactable<Integer> implements IForgeEnergyHandler {

    public ForgeEnergyHandler(int size) {
        super(size);
    }

    public ForgeEnergyHandler(Integer... iterable) {
        super(iterable);
    }

    public ForgeEnergyHandler(Collection<Integer> iterable)
    {
        super(iterable);
    }
}
