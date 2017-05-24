package loordgek.itemhandlerv2.itemhandler.wrappers;

import loordgek.itemhandlerv2.itemhandler.IItemFilter;
import loordgek.itemhandlerv2.itemhandler.IItemHandler;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.function.Predicate;

public class ISidedInventoryWrapper implements IItemHandler {
    private final ISidedInventory inventory;
    private final EnumFacing facing;

    public ISidedInventoryWrapper(ISidedInventory inventory, EnumFacing facing) {
        this.inventory = inventory;
        this.facing = facing;
    }


    @Override
    public int size() {
        return inventory.getSizeInventory();
    }

    @Override
    public boolean isStackValid(@Nonnull ItemStack stack) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack insert(@Nonnull ItemStack stack, boolean simulate) {
        return null;
    }

    @Nonnull
    @Override
    public ItemStack extract(Predicate<ItemStack> filter, int min, int max, boolean simulate) {
        return null;
    }

    @Override
    public int getLimit() {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
       return inventory.getStackInSlot(slot);
    }
}
