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
    public boolean isStackValid(ItemStack stack) {
        for (int i = 0; i < size(); i++) {
            if (getInventory().isItemValidForSlot(i, stack))
                return true;
        }
        return false;
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
        if (slot.isPresent()){
            int index = slot.getAsInt();

            ItemStack existing = getStackInSlot(index);


            if (!getInventory().isItemValidForSlot(index, stack))
                return new InsertTransaction(ItemStack.EMPTY, stack);

            int limit = getStackLimit(stack);

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
                    getInventory().setInventorySlotContents(index,  transaction.getInsertedStack());
                    getInventory().markDirty();
                } else {
                    transaction = ItemHandlerHelperV2.insertIntoExistingStack(existing, stack, false);
                    getInventory().markDirty();

                }
            }
            return transaction;
        }
        else{
            for (int i = 0; i < size(); i++) {
                int limit = getStackLimit(stack);
                if (getInventory().isItemValidForSlot(i, stack)){
                    ItemStack existing = getStackInSlot(i);

                    InsertTransaction transaction;
                    if (existing.isEmpty()){
                        transaction = ItemHandlerHelperV2.split(stack, limit);
                        if (simulate) {
                            getInventory().setInventorySlotContents(i, transaction.getInsertedStack());
                            getInventory().markDirty();
                        }
                    }

                    else {
                        transaction = ItemHandlerHelperV2.insertIntoExistingStack(existing, stack, simulate);
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
        return null;
    }

    @Override
    public List<IItemHandlerObserver> itemObserverList() {
        return null;
    }

    protected IInventory getInventory() {
        return inventory;
    }
}
