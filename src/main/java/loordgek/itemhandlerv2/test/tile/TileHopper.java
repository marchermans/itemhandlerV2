package loordgek.itemhandlerv2.test.tile;

import loordgek.itemhandlerv2.itemhandler.CapabilityIItemHander;
import loordgek.itemhandlerv2.itemhandler.Itemhandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileHopper extends TileEntity implements ITickable{
    private int cooldown = 20;
    private final Itemhandler itemStacks = new Itemhandler(6);
    @Override
    public void update() {

    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityIItemHander.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityIItemHander.ITEM_HANDLER_CAPABILITY){
            return CapabilityIItemHander.ITEM_HANDLER_CAPABILITY.cast(itemStacks);
        }
        return super.getCapability(capability, facing);
    }
}
