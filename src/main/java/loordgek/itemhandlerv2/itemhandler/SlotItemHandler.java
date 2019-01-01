package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotItemHandler extends Slot {
    private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    private final IItemHandler handler;

    public SlotItemHandler(IItemHandler handler, int index, int xPosition, int yPosition) {
        super(emptyInventory, index, xPosition, yPosition);
        this.handler = handler;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        ITransaction transaction = getItemHandler().insert(getSlotIndex(), stack, true);
        transaction.cancel();
        return !transaction.getType().isInvalid();
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
        return getItemHandler().getStackInSlot(getSlotIndex());
    }


    @Override
    public void putStack(@Nonnull ItemStack stack) {
        getItemHandler().insert(getSlotIndex(), stack, true).confirm();
    }

    @Override
    public int getSlotStackLimit() {
        return getItemHandler().getSlotLimit(getSlotIndex());
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int amount) {
        return getItemHandler().extract(getSlotIndex(), amount).confirm();
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn) {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        ITransaction transaction = getItemHandler().extract(getSlotIndex() , 1);
        transaction.cancel();
        return !transaction.getType().isInvalid();
    }

    @Override
    public boolean isSameInventory(Slot other)     {
        return other instanceof SlotItemHandler && ((SlotItemHandler) other).getItemHandler() == this.getItemHandler();
    }

    private IItemHandler getItemHandler() {
        return handler;
    }
}
