package loordgek.itemhandlerv2.itemhandler;

import com.google.common.collect.Range;
import loordgek.itemhandlerv2.itemholder.IItemHolder;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class ItemHandler implements IItemHandler {
    private final IItemHolder holder;

    public ItemHandler(IItemHolder holder) {
        this.holder = holder;
    }

    @Override
    public boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot) {
        return holder.isStackValidForSlot(stack, slot);
    }

    @Override
    public boolean canExtractStackFormSlot(@Nonnull ItemStack stack, int slot) {
        return holder.canExtractStackFormSlot(stack, slot);
    }

    @Override
    public int size() {
        return holder.getSlotCount();
    }

    @Override
    public int getSlotLimit() {
        return 64;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return holder.getStack(slot);
    }

    @Nonnull
    @Override
    public InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate) {
        int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
        int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
        for (int i = minSlot; i < maxSlot; i++) {
            if (isStackValidForSlot(stack, i)) {
                ItemStack existing = getStackInSlot(i);
                InsertTransaction transaction;
                if (existing.isEmpty()){
                    transaction = ItemHandlerHelperV2.split(stack, getSlotLimit());
                }
                else
                    transaction = ItemHandlerHelperV2.insertIntoExistingStack(existing, stack, getFreeSpaceForSlot(i));
                if (!simulate){
                    holder.putStack(i, transaction.getInsertedStack(), false);
                }
                if (transaction.getInsertedStack().isEmpty())
                    return transaction;
            }
        }
        return new InsertTransaction(ItemStack.EMPTY, stack);
    }

    @Nonnull
    @Override
    public ItemStack extract(Range<Integer> slotRange, Predicate<ItemStack> filter, int amount, boolean simulate) {
        int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
        int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
        for (int i = minSlot; i < maxSlot; i++) {
            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty() && canExtractStackFormSlot(stack, i) && filter.test(stack)) {
                ItemStack extracted = holder.decreaseStack(i, amount, simulate);
                if (!extracted.isEmpty())
                    return extracted;
            }
        }
        return ItemStack.EMPTY;
    }
}
