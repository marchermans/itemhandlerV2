package loordgek.itemhandlerv2.test;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = "testmod")
public class EvnetHandler {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof TileEntityChest){

            event.addCapability(new ResourceLocation(Ref.modinfo.modid, "test"));
        }
    }
}
