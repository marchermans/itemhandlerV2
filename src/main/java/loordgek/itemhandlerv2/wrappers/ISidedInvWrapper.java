package loordgek.itemhandlerv2.wrappers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class ISidedInvWrapper extends IInvWrapperBase {
    private final ISidedInventory inventory;
    private final EnumFacing facing;

    public ISidedInvWrapper(ISidedInventory inventory, EnumFacing facing) {
        this.inventory = inventory;
        this.facing = facing;
    }

    @Override
    public boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot) {
        return inventory.canInsertItem(slot, stack, facing);
    }

    @Override
    public boolean canExtractStackFormSlot(@Nonnull ItemStack stack, int slot) {
        return inventory.canExtractItem(slot, stack, facing);
    }

    @Override
    public int size() {
        return inventory.getSlotsForFace(facing).length;
    }

    @Override
    public int getSlotLimit(int slot) {
        return getInventory().getInventoryStackLimit();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(inventory.getSlotsForFace(facing)[slot]);
    }

    @Override
    protected IInventory getInventory() {
        return inventory;
    }
}
