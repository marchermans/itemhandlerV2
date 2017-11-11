package loordgek.itemhandlerv2.wrappers;

import loordgek.itemhandlerv2.IItemHandler;
import loordgek.itemhandlerv2.IItemHandlerObserver;
import loordgek.itemhandlerv2.InsertTransaction;
import loordgek.itemhandlerv2.ItemHandlerHelperV2;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;

public class InvWrapper implements IItemHandler {

    private final IInventory inventory;

    public InvWrapper(IInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public int size() {
        return getInventory().getSizeInventory();
    }

    @Override
    public int getSlotLimit() {
        return getInventory().getInventoryStackLimit();
    }

    @Override
    public boolean isStackValidForSlot(ItemStack stack, int slot) {
        return getInventory().isItemValidForSlot(slot, stack);
    }

    @Override
    public void clear() {
        getInventory().clear();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return getInventory().getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public InsertTransaction insert(@Nonnull ItemStack stack, OptionalInt slot, boolean simulate) {
        if (stack.isEmpty())
            return new InsertTransaction(ItemStack.EMPTY, ItemStack.EMPTY);
        if (slot.isPresent()) {
            int index = slot.getAsInt();

            ItemStack existing = getStackInSlot(index);


            if (!getInventory().isItemValidForSlot(index, stack))
                return new InsertTransaction(ItemStack.EMPTY, stack);

            int limit = stack.getCount();

            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                    return new InsertTransaction(ItemStack.EMPTY, stack);

                limit -= existing.getCount();
            }

            if (limit <= 0)
                return new InsertTransaction(ItemStack.EMPTY, stack);

            InsertTransaction transaction = ItemHandlerHelperV2.split(stack, limit);
            if (!simulate) {
                if (existing.isEmpty()) {
                    getInventory().setInventorySlotContents(index, transaction.getInsertedStack());
                    getInventory().markDirty();
                } else {
                    transaction = ItemHandlerHelperV2.insertIntoExistingStack(existing, stack, limit, false);
                    getInventory().markDirty();

                }
            }
            return transaction;
        } else {

            for (int i = 0; i < size(); i++) {

                ItemStack existing = getStackInSlot(i);


                if (!getInventory().isItemValidForSlot(i, stack))
                    return new InsertTransaction(ItemStack.EMPTY, stack);

                int limit = stack.getCount();

                if (!existing.isEmpty()) {
                    if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                        return new InsertTransaction(ItemStack.EMPTY, stack);

                    limit -= existing.getCount();
                }

                if (limit <= 0)
                    return new InsertTransaction(ItemStack.EMPTY, stack);

                InsertTransaction transaction = ItemHandlerHelperV2.split(stack, limit);
                if (!simulate) {
                    if (existing.isEmpty()) {
                        getInventory().setInventorySlotContents(i, transaction.getInsertedStack());
                        getInventory().markDirty();
                    } else {
                        transaction = ItemHandlerHelperV2.insertIntoExistingStack(existing, stack, limit, false);
                        getInventory().markDirty();

                    }
                    return transaction;
                }
            }
        }
        return new InsertTransaction(ItemStack.EMPTY, stack);
    }

    @Nonnull
    @Override
    public ItemStack extract(@Nonnull Predicate<ItemStack> filter, OptionalInt slot, int amount, boolean simulate) {
        if (amount == 0) return ItemStack.EMPTY;
        if (slot.isPresent()) {

            ItemStack stackInSlot = getStackInSlot(slot.getAsInt());

            if (stackInSlot.isEmpty())
                return ItemStack.EMPTY;

            if (!filter.test(stackInSlot))
                return ItemStack.EMPTY;

            if (simulate) {
                if (stackInSlot.getCount() < amount) {
                    return stackInSlot.copy();
                } else {
                    ItemStack copy = stackInSlot.copy();
                    copy.setCount(amount);
                    return copy;
                }
            } else {
                int m = Math.min(stackInSlot.getCount(), amount);

                ItemStack decrStackSize = getInventory().decrStackSize(slot.getAsInt(), m);
                getInventory().markDirty();
                return decrStackSize;
            }

        } else {
            for (int i = 0; i < size(); i++) {

                ItemStack stackInSlot = getStackInSlot(i);
                if (stackInSlot.isEmpty() && !filter.test(stackInSlot)) continue;
                if (simulate) {
                    if (stackInSlot.getCount() < amount) {
                        return stackInSlot.copy();
                    } else {
                        ItemStack copy = stackInSlot.copy();
                        copy.setCount(amount);
                        return copy;
                    }
                } else {
                    int m = Math.min(stackInSlot.getCount(), amount);

                    ItemStack decrStackSize = getInventory().decrStackSize(i, m);
                    if (decrStackSize.isEmpty()) continue;
                    getInventory().markDirty();
                    return decrStackSize;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public List<IItemHandlerObserver> itemObserverList() {
        return null;
    }

    protected IInventory getInventory() {
        return inventory;
    }
}
