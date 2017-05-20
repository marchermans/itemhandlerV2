package loordgek.itemhandlerv2.itemhandler.wrappers;

import loordgek.itemhandlerv2.itemhandler.IItemFilter;
import loordgek.itemhandlerv2.itemhandler.IItemHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class IIinventoryWrapper implements IItemHandler {
    private final IInventory inventory;
    private final Iterator<ItemStack> itemStackIterator;

    public IIinventoryWrapper(IInventory inventory, Iterator<ItemStack> itemStackIterator) {
        this.inventory = inventory;
        this.itemStackIterator = itemStackIterator;
    }

    @Override
    public int size() {
        return inventory.getSizeInventory();
    }

    @Override
    public int getLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isStackValid(@Nonnull ItemStack stack) {
        for (int i = 0; i < size(); i++) {
            if (inventory.isItemValidForSlot(i, stack))
                return true;
        }
        return false;
    }



    @Nonnull
    @Override
    public ItemStack insert(@Nonnull ItemStack stack, boolean simulate) {
        ItemStack remaining = stack.copy();
        int firstEmpty = -1;

        // Try to fill existing slots first
        for (int i = 0; i < size(); i++)
        {
            ItemStack slot = inventory.getStackInSlot(i);
            if (slot.isEmpty())
            {
                int max = Math.min(remaining.getMaxStackSize(), inventory.getInventoryStackLimit());
                int transfer = Math.min(remaining.getCount(), max - slot.getCount());
                if (transfer > 0 && ItemStack.areItemsEqual(slot, remaining) && ItemStack.areItemStackTagsEqual(slot, remaining))
                {
                    slot.grow(transfer);
                    remaining.shrink(transfer);
                    if (remaining.getCount() == 0)
                        break;
                }
            }
            else if (firstEmpty < 0)
            {
                firstEmpty = i;
            }
        }

        // Then place the remaining items in the first available empty slot
        if (remaining.getCount() > 0 && firstEmpty >= 0)
        {
            for (int i = 0; i < inventory.getSizeInventory(); i++)
            {
                ItemStack slot = inventory.getStackInSlot(i);
                if (slot.isEmpty())
                {
                    int max = Math.min(remaining.getMaxStackSize(), inventory.getInventoryStackLimit());
                    int transfer = Math.min(remaining.getCount(), max);
                    if (transfer > 0)
                    {
                        ItemStack insert = remaining.copy();
                        insert.setCount(transfer);
                        inventory.setInventorySlotContents(i, insert);
                        remaining.shrink(transfer);
                    }
                }
            }
        }

        if (remaining.getCount() > 0)
            return remaining;

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extract(@Nonnull IItemFilter filter, int min, int max, boolean simulate) {
        for (int i = 0; i < size(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (filter.test(stack) && stack.getCount() >= min) {
                ItemStack pulled = inventory.decrStackSize(i, Math.min(max, stack.getCount()));
                if (stack.isEmpty())
                    inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                return pulled;
            }
            inventory.getStackInSlot(i);

        }
        return ItemStack.EMPTY;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return itemStackIterator;
    }
}
