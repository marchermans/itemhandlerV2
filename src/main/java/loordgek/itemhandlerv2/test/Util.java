package loordgek.itemhandlerv2.test;

import loordgek.itemhandlerv2.CapbilityItemHandler;
import loordgek.itemhandlerv2.IItemHandler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Util {

    public static IItemHandler gethandler(World world, BlockPos pos, EnumFacing facing){
        if (world.getTileEntity(pos) != null){
           if (world.getTileEntity(pos).hasCapability(CapbilityItemHandler.ITEM_HANDLER_CAPABILITY, facing)){
              return world.getTileEntity(pos).getCapability(CapbilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);
           }
        }
        return null;
    }
}
