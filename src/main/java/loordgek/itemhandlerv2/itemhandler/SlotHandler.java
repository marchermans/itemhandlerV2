package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotHandler extends Slot {
    private static final IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    private final IItemSlotHandler slotHandler;

    public SlotHandler(IItemSlotHandler slotHandler, int index, int xPosition, int yPosition) {
        super(emptyInventory, index, xPosition, yPosition);
        this.slotHandler = slotHandler;
    }

    @Override
    @Nonnull
    public ItemStack onTake(EntityPlayer thePlayer, @Nonnull ItemStack stack) {
        slotHandler.decrStackSize(stack.getCount(), getSlotIndex());
        return stack;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        if (!stack.isEmpty())
            slotHandler.insertStack(stack, getSlotIndex());
    }

    @Override
    public int getSlotStackLimit() {
        return slotHandler.getSlotLimit();
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
        return slotHandler.getStackInSlot(getSlotIndex());
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return slotHandler.getStackLimit(getSlotIndex(), stack);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return slotHandler.isStackValid(stack);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
       return slotHandler.canTakeStack(getSlotIndex(), playerIn);
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn) {
        return false;
    }

    public IItemSlotHandler getSlotHandler() {
        return slotHandler;
    }

    @Override
    public boolean isSameInventory(Slot other) {
       return other instanceof IItemSlotHandler && ((SlotHandler)other).getSlotHandler() == slotHandler;
    }
}
