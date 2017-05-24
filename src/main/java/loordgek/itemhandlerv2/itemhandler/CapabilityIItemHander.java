package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class CapabilityIItemHander {

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    public static void register(){
        CapabilityManager.INSTANCE.register(IItemHandler.class, new Capability.IStorage<IItemHandler>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IItemHandler> capability, IItemHandler instance, EnumFacing side) {
                if (!(instance instanceof INBTSerializable))
                    throw new RuntimeException();
                else return ((INBTSerializable<NBTTagList>)instance).serializeNBT();

            }

            @Override
            public void readNBT(Capability<IItemHandler> capability, IItemHandler instance, EnumFacing side, NBTBase nbt) {
                if (!(instance instanceof INBTSerializable))
                    throw new RuntimeException();
                else ((INBTSerializable<NBTTagList>)instance).deserializeNBT((NBTTagList)nbt);

            }
        }, () -> new Itemhandler(1));
    }
}
