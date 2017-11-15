package loordgek.itemhandlerv2.itemhandler;


import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class Storage implements Capability.IStorage<IItemHandler> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IItemHandler> capability, IItemHandler instance, EnumFacing side) {
        return null;
    }

    @Override
    public void readNBT(Capability<IItemHandler> capability, IItemHandler instance, EnumFacing side, NBTBase nbt) {

    }
}
