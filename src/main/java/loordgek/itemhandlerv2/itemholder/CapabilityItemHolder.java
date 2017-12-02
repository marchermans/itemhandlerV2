package loordgek.itemhandlerv2.itemholder;

import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityItemHolder {
    /**
     * Only use this for inner mod things
     */
    @CapabilityInject(IItemHolder.class)
    public static IItemHolder ITEM_HOLDER_CAPABILITY = null;
}
