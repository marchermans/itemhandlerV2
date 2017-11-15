package loordgek.itemhandlerv2.wrappers;

import com.google.common.collect.Range;
import loordgek.itemhandlerv2.itemhandler.IItemHandler;
import loordgek.itemhandlerv2.itemhandler.InsertTransaction;
import loordgek.itemhandlerv2.itemhandler.ItemHandlerHelperV2;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public abstract class IInvWrapperBase implements IItemHandler {
    @Override
    public int size() {
        return getInventory().getSizeInventory();
    }

    @Override
    public int getSlotLimit() {
        return getInventory().getInventoryStackLimit();
    }

    @Override
    public boolean isStackValid(@Nonnull ItemStack stack) {
        for (int i = 0; i < size(); i++) {
            if (isStackValidForSlot(stack, i)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractStack(@Nonnull ItemStack stack) {
        for (int i = 0; i < size(); i++) {
            if (canExtractStackFormSlot(stack, i)) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return getInventory().getStackInSlot(slot);
    }


    public boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot) {
        return getInventory().isItemValidForSlot(slot, stack);
    }


    public boolean canExtractStackFormSlot(@Nonnull ItemStack stack, int slot) {
        return true;
    }

    @Nonnull
    @Override
    public InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return new InsertTransaction(ItemStack.EMPTY, ItemStack.EMPTY);
        int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
        int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
        for (int i = minSlot; i < maxSlot; i++) {
            ItemStack stackInSlot = getStackInSlot(i);
            if (!isStackValidForSlot(stack, i)) continue;
            if (stackInSlot.isEmpty()) {
                InsertTransaction transaction = ItemHandlerHelperV2.split(stack, getFreeSpaceForSlot(i));
                if (simulate) {
                    return transaction;
                } else {
                    getInventory().setInventorySlotContents(i, transaction.getInsertedStack());
                    getInventory().markDirty();
                    return transaction;
                }
            } else {
                if (!ItemHandlerHelper.canItemStacksStack(stackInSlot, stack)) continue;
                else {
                    InsertTransaction transaction = ItemHandlerHelperV2.split(stack, getFreeSpaceForSlot(i));
                    if (transaction.getInsertedStack().isEmpty()) continue;
                    else {
                        if (simulate) {
                            return transaction;
                        } else {
                            getInventory().setInventorySlotContents(i, transaction.getInsertedStack());
                            getInventory().markDirty();
                            return transaction;
                        }
                    }
                }
            }
        }

        return new InsertTransaction(ItemStack.EMPTY, stack);
    }

    @Nonnull
    @Override
    public ItemStack extract(Range<Integer> slotRange, Predicate<ItemStack> filter, int amount, boolean simulate) {
        if (amount == 0) return ItemStack.EMPTY;
        int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
        int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
        for (int i = minSlot; i < maxSlot; i++) {
            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty() && canExtractStackFormSlot(stack, i) && filter.test(stack)) {
                if (simulate) {
                    if (stack.getCount() < amount) {
                        return stack.copy();
                    } else {
                        ItemStack copy = stack.copy();
                        copy.setCount(amount);
                        return copy;
                    }
                } else {
                    int m = Math.min(stack.getCount(), amount);

                    ItemStack decrStackSize = getInventory().decrStackSize(i, m);
                    getInventory().markDirty();
                    return decrStackSize;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    protected abstract IInventory getInventory();
}
