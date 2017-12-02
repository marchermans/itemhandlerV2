package loordgek.itemhandlerv2.test;

import com.google.common.collect.Range;
import loordgek.itemhandlerv2.itemhandler.CapbilityItemHandler;
import loordgek.itemhandlerv2.itemhandler.IItemHandler;
import loordgek.itemhandlerv2.itemhandler.IItemHandlerIterator;
import loordgek.itemhandlerv2.itemhandler.InsertTransaction;
import loordgek.itemhandlerv2.wrappers.CombinedInvWrapper;
import loordgek.itemhandlerv2.wrappers.RangedWrapper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = "testmod")
public class EventHandler {

    @SubscribeEvent
    public static void onAttachCapabilitiesTileEntity(AttachCapabilitiesEvent<TileEntity> event) {
        ResourceLocation location = new ResourceLocation(Ref.modinfo.modid, "test");
        if (event.getCapabilities().containsKey(location)) return;
        if (event.getObject() instanceof TileEntityChest) {
            event.addCapability(location, new CapProvider((IInventory) event.getObject()));
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer)
            event.addCapability(new ResourceLocation(Ref.modinfo.modid, "test2"), new CapPlayerProvider((EntityPlayer) event.getObject()));
    }

    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = player.world;
        BlockPos hitpos = event.getPos();
        if (world.isRemote) return;
        Block block = world.getBlockState(event.getPos()).getBlock();
        if (block == Blocks.CHEST) {
            ItemStack heldstack = player.getHeldItem(event.getHand());
            if (heldstack.isEmpty()) return;

            Logger log = TestMod.logger;
            IItemHandler playerinv = player.getCapability(CapbilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            IItemHandler chestrinv = world.getTileEntity(hitpos).getCapability(CapbilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            IItemHandler chestrinv0to8 = new RangedWrapper(chestrinv, 0, 8);
            IItemHandler chestrinv9to17 = new RangedWrapper(chestrinv, 9, 17);
            IItemHandler chestrinv18to26 = new RangedWrapper(chestrinv, 18, 26);
            IItemHandler combined = new CombinedInvWrapper(chestrinv0to8, chestrinv9to17, chestrinv18to26);

            if (heldstack.getItem() == Items.STICK) {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString(Float.toString(chestrinv.calcRedStoneFromInventory(Range.all(),100, false))));
                player.sendMessage(new TextComponentString(Float.toString(chestrinv.calcRedStoneFromInventory(Range.singleton(1), 100, false))));
                player.sendMessage(new TextComponentString(Float.toString(chestrinv0to8.calcRedStoneFromInventory(Range.all(),100, false))));
                player.sendMessage(new TextComponentString(Float.toString(chestrinv0to8.calcRedStoneFromInventory(Range.all(),100, true))));
            }

            if (heldstack.getItem() == Items.BLAZE_ROD) {
                event.setCanceled(true);

                InsertTransaction transaction = chestrinv18to26.insert(Range.singleton(0), new ItemStack(Items.GLOWSTONE_DUST, 48), false);
                log.info(transaction.getInsertedStack());
                log.info(transaction.getLeftoverStack());
            }
            if (heldstack.getItem() == Items.GLOWSTONE_DUST){
                event.setCanceled(true);

                IItemHandlerIterator iteratorNonEmpty = chestrinv.itemHandlerIterator();
                while (iteratorNonEmpty.hasNext()){
                    ItemStack stack = iteratorNonEmpty.next();
                    if (!stack.isEmpty()){
                        log.info(stack + " at index " + Integer.toString(iteratorNonEmpty.previousIndex()));
                    }
                }
                log.info("complete itr");
            }
        }
    }
}
