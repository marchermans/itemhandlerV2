package loordgek.itemhandlerv2.test;

import loordgek.itemhandlerv2.test.Gui.GuiHopper;
import loordgek.itemhandlerv2.test.container.ContainerHopper;
import loordgek.itemhandlerv2.test.tile.TileHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    public enum GUIIDS{
        HOPPER
    }
    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, x));
        if (ID == GUIIDS.HOPPER.ordinal()){
            return new ContainerHopper((TileHopper)tileEntity);
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, x));
        if (ID == GUIIDS.HOPPER.ordinal()){
            return new GuiHopper(new ContainerHopper((TileHopper)tileEntity));
        }
        return null;
    }
}
