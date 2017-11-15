package loordgek.itemhandlerv2.itemholder;

import loordgek.itemhandlerv2.itemhandler.ItemHandlerHelperV2;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotItemHolder extends Slot {
    private final IItemHolder itemHolder;
    public SlotItemHolder(IItemHolder itemHolder, int index, int xPosition, int yPosition) {
        super(ItemHandlerHelperV2.emptyInventory, index, xPosition, yPosition);
        this.itemHolder = itemHolder;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
       return itemHolder.isStackValidForSlot(stack, getSlotIndex());
    }

    @Nonnull
    @Override
    public ItemStack getStack() {
        return itemHolder.getStack(getSlotIndex());
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        itemHolder.putStack(getSlotIndex(), stack, false);
    }

    @Override
    public int getSlotStackLimit() {
        return super.getSlotStackLimit();
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int amount) {
        return itemHolder.decreaseStack(getSlotIndex(), amount, false);
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn) {
        return false;
    }
}
