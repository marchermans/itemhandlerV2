package loordgek.itemhandlerv2;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class ItemHandlerHelperV2 {

    public static InsertTransaction split(@Nonnull ItemStack stack, int size) {
        int i = Math.min(stack.getCount(), size);
        ItemStack insert = ItemHandlerHelper.copyStackWithSize(stack, i);

        ItemStack leftover = stack.copy();
        leftover.setCount(stack.getCount() - insert.getCount());
        return new InsertTransaction(insert, leftover);
    }

    public static InsertTransaction insertIntoExistingStack(@Nonnull ItemStack existing, ItemStack stack, int limit, boolean simulate) {
        if (!ItemHandlerHelper.canItemStacksStack(existing, stack)) {
            return new InsertTransaction(ItemStack.EMPTY, stack);
        }
        int freeSpace = Math.min(limit, stack.getCount());
        if (!simulate)
            existing.grow(freeSpace);
        return split(stack, freeSpace);
    }

    public static InsertTransaction insertIntoTargetStack(ItemStack stack, IItemHandler itemHandler, int slot, boolean simulate) {
        if (itemHandler.isStackValidForSlot(stack, slot)) {
            ItemStack existing = itemHandler.getStackInSlot(slot);

            if (existing.isEmpty()) {
                return split(stack, itemHandler.getFreeSpaceForSlot(slot));

            } else return insertIntoExistingStack(existing, stack, itemHandler.getFreeSpaceForSlot(slot), simulate);
        }
        return new InsertTransaction(ItemStack.EMPTY, stack);
    }
}
