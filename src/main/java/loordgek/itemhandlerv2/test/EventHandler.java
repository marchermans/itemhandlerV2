package loordgek.itemhandlerv2.test;

import com.google.common.collect.Range;
import loordgek.itemhandlerv2.CapbilityItemHandler;
import loordgek.itemhandlerv2.IItemHandler;
import loordgek.itemhandlerv2.InsertTransaction;
import loordgek.itemhandlerv2.filter.ItemStackFilter;
import loordgek.itemhandlerv2.filter.OreDictFilter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.util.OptionalInt;

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

            Logger log = testmod.logger;
            IItemHandler playerinv = player.getCapability(CapbilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            IItemHandler chestrinv = world.getTileEntity(hitpos).getCapability(CapbilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

            if (heldstack.getItem() == Items.STICK) {
                event.setCanceled(true);
                player.sendMessage(new TextComponentString(Float.toString(chestrinv.calcRedStoneFromInventory(100))));
            }

            if (heldstack.getItem() == Items.BLAZE_ROD){
                event.setCanceled(true);
                IItemHandler itemHandler = null;
                for (EnumFacing facing : EnumFacing.VALUES){
                    itemHandler = Util.gethandler(world, hitpos.offset(facing), facing);
                    if (itemHandler != null) break;
                }
                if (itemHandler != null){
                    InsertTransaction transaction = itemHandler.insert(chestrinv.extract(stack -> true, OptionalInt.empty(), 2, false), OptionalInt.empty(), false);
                    log.info(transaction.getInsertedStack());
                    log.info(transaction.getLeftoverStack());
                    ItemStackFilter.filterBuilder().withStackSize(Range.closed(10, 20)).build().and(new OreDictFilter("stairWood"));
                }
            }
        }
    }
}
