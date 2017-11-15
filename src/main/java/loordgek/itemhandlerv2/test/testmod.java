package loordgek.itemhandlerv2.test;

import loordgek.itemhandlerv2.itemhandler.IItemHandler;
import loordgek.itemhandlerv2.itemhandler.ItemHandler;
import loordgek.itemhandlerv2.itemhandler.Storage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "testmod")
public class testmod {

    public static Logger logger;

    @Mod.EventHandler
    public void onFMLPreInitialization(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        CapabilityManager.INSTANCE.register(IItemHandler.class, new Storage(), ItemHandler.class);
    }

}
