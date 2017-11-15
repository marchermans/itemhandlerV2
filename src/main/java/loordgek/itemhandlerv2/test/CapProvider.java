package loordgek.itemhandlerv2.test;

import loordgek.itemhandlerv2.itemhandler.CapbilityItemHandler;
import loordgek.itemhandlerv2.wrappers.InvWrapper;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapProvider implements ICapabilityProvider {

    private final InvWrapper inventory;

    public CapProvider(IInventory inventory) {
        this.inventory = new InvWrapper(inventory);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapbilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapbilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return CapbilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory);
        return null;
    }
}
