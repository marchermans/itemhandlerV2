package loordgek.itemhandlerv2.test;

import loordgek.itemhandlerv2.itemhandler.CapbilityItemHandler;
import loordgek.itemhandlerv2.wrappers.InvWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapPlayerProvider implements ICapabilityProvider {

    private final EntityPlayer player;
    private InvWrapper playerinv;

    public CapPlayerProvider(EntityPlayer player) {
        this.player = player;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        if (playerinv == null){
            playerinv = new InvWrapper(player.inventory);
        }
        return capability == CapbilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (playerinv == null){
            playerinv = new InvWrapper(player.inventory);
        }
        return capability == CapbilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapbilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(playerinv) : null;
    }
}
