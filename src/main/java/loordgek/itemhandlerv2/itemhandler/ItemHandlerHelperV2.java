package loordgek.itemhandlerv2.itemhandler;

import com.google.common.collect.Range;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class ItemHandlerHelperV2 {
    public static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);


    public static InsertTransaction split(@Nonnull ItemStack stack, int size) {
        int i = Math.min(stack.getCount(), size);
        ItemStack insert = ItemHandlerHelper.copyStackWithSize(stack, i);

        ItemStack leftover = stack.copy();
        leftover.setCount(stack.getCount() - insert.getCount());
        return new InsertTransaction(insert, leftover);
    }

    public static InsertTransaction insertIntoExistingStack(@Nonnull ItemStack existing, ItemStack stack, int limit) {
        if (!ItemHandlerHelper.canItemStacksStack(existing, stack)) {
            return new InsertTransaction(ItemStack.EMPTY, stack);
        }
        int freeSpace = Math.min(limit, stack.getCount());
        return split(stack, freeSpace);
    }

    public static InsertTransaction insertIntoTargetStack(ItemStack stack, IItemHandler itemHandler, int slot) {
        if (itemHandler.isStackValid(stack)) {
            ItemStack existing = itemHandler.getStackInSlot(slot);

            if (existing.isEmpty()) {
                return split(stack, itemHandler.getFreeSpaceForSlot(slot));

            } else return insertIntoExistingStack(existing, stack, itemHandler.getFreeSpaceForSlot(slot));
        }
        return new InsertTransaction(ItemStack.EMPTY, stack);
    }

    public static boolean isRangeSlotLess(Range<Integer> range){
        return !range.hasLowerBound() && range.hasUpperBound();
    }
}
