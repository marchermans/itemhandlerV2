package loordgek.itemhandlerv2.test;

import loordgek.itemhandlerv2.test.block.BlockHopper;
import loordgek.itemhandlerv2.test.tile.TileHopper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(
        modid = ItemhandlerV2.MOD_ID,
        name = ItemhandlerV2.MOD_NAME,
        version = ItemhandlerV2.VERSION
)
@Mod.EventBusSubscriber
public class ItemhandlerV2 {
    @Mod.Instance
    public static ItemhandlerV2 itemhandlerV2;

    public static BlockHopper blockHopper = new BlockHopper();

    public static final String MOD_ID = "itemhandlerV2";
    public static final String MOD_NAME = "ItemhandlerV2";
    public static final String VERSION = "1.0-SNAPSHOT";

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @SubscribeEvent
    public void blockRegister(RegistryEvent.Register<Block> event) {
        GameRegistry.registerTileEntity(TileHopper.class, "tilehopper");
        event.getRegistry().register(blockHopper);
    }

    @SubscribeEvent
    public void itemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(blockHopper));
    }
}
