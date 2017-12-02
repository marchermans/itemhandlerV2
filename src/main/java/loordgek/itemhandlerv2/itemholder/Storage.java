package loordgek.itemhandlerv2.itemholder;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class Storage implements Capability.IStorage<IItemHolder> {
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IItemHolder> capability, IItemHolder instance, EnumFacing side) {
        if (!(instance instanceof INBTSerializable))
            throw new RuntimeException();
        else return ((INBTSerializable)instance).serializeNBT();
    }

    @Override
    public void readNBT(Capability<IItemHolder> capability, IItemHolder instance, EnumFacing side, NBTBase nbt) {
        if (!(instance instanceof INBTSerializable))
            throw new RuntimeException();
        else  ((INBTSerializable)instance).deserializeNBT(nbt);
    }
}
