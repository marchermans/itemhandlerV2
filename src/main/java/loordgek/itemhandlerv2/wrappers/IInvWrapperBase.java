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
    public int getSlotLimit(int slot) {
        return getInventory().getInventoryStackLimit();
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
        if (ItemHandlerHelperV2.isRangeSingleton(slotRange)) {
            int slot = slotRange.lowerEndpoint();
            ItemStack existing = getStackInSlot(slot);

            InsertTransaction transaction;
            if (isStackValidForSlot(existing, slot)) {
                if (existing.isEmpty()) {
                    transaction = ItemHandlerHelperV2.split(stack, getFreeSpaceForSlot(slot));
                    if (simulate){
                        return transaction;
                    }
                    else {
                        getInventory().setInventorySlotContents(slot, transaction.getInsertedStack());
                        getInventory().markDirty();
                        return transaction;
                    }
                } else {
                    transaction = ItemHandlerHelperV2.insertIntoExistingStack(existing, stack, getFreeSpaceForSlot(slot));
                    if (simulate){
                        return transaction;
                    }
                    else {
                        getInventory().getStackInSlot(slot).grow(transaction.getInsertedStack().getCount());
                        getInventory().markDirty();
                        return transaction;
                    }
                }
            }
        } else {
            int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
            int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
            for (int i = minSlot; i < maxSlot; i++) {
                ItemStack existing = getStackInSlot(i);
                if (!isStackValidForSlot(stack, i)) continue;
                if (existing.isEmpty()) {
                    InsertTransaction transaction = ItemHandlerHelperV2.split(stack, getFreeSpaceForSlot(i));
                    if (simulate) {
                        return transaction;
                    } else {
                        getInventory().setInventorySlotContents(i, transaction.getInsertedStack());
                        getInventory().markDirty();
                        return transaction;
                    }
                } else {
                    if (!ItemHandlerHelper.canItemStacksStack(existing, stack)) continue;
                    else {
                        InsertTransaction transaction = ItemHandlerHelperV2.split(stack, getFreeSpaceForSlot(i));
                        if (transaction.getInsertedStack().isEmpty()) continue;
                        else {
                            if (simulate) {
                                return transaction;
                            } else {
                                getInventory().getStackInSlot(i).grow(transaction.getInsertedStack().getCount());
                                getInventory().markDirty();
                                return transaction;
                            }
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

        if (ItemHandlerHelperV2.isRangeSingleton(slotRange)) {
            int slot = slotRange.lowerEndpoint();
            ItemStack stack = getStackInSlot(slot);
            if (stack.isEmpty()) return ItemStack.EMPTY;
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

                ItemStack decrStackSize = getInventory().decrStackSize(slot, m);
                getInventory().markDirty();
                return decrStackSize;
            }
        } else {
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
        }
        return ItemStack.EMPTY;
    }

    protected abstract IInventory getInventory();
}
