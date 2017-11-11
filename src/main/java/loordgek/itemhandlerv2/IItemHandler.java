package loordgek.itemhandlerv2;


import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.OptionalInt;
import java.util.function.Predicate;

public interface IItemHandler extends IItemHandlerObservable {

    int size();

    default boolean isStackValidForSlot(ItemStack stack, int slot) {
        return true;
    }

    default boolean canExtractFormInv() {
        return true;
    }

    default IItemHandlerIterator itemhandlerIterator(boolean skipEmpty) {
        return new ItemHandlerIterator(this, skipEmpty);
    }

    /**
     * @return A Integer in the range [0,scale] representing how "full" this inventory is.
     */
    //todo give me a better name
    default float calcRedStoneFromInventory(int scale) {

        float proportion = 0.0F;

        for (int j = 0; j < size(); ++j) {
            ItemStack itemstack = getStackInSlot(j);

            if (!itemstack.isEmpty()) {
                proportion += (float) itemstack.getCount() / (float) getStackLimit(itemstack);
            }
        }

        proportion = proportion / (float) size();

        return (proportion * scale);
    }

    void clear();

    int getSlotLimit();

    default int getStackLimit(ItemStack stack) {
        return Math.min(stack.getMaxStackSize(), getSlotLimit());
    }

    default int getFreeSpaceForSlot(int slot){
        ItemStack existing = getStackInSlot(slot);
        if (!existing.isEmpty()){
            if (!existing.isStackable()){
                return 0;
            }
            else return getSlotLimit() - existing.getCount();
        }
        return getSlotLimit();
    }

    @Nonnull
    ItemStack getStackInSlot(int slot);

    /**
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack should not be modified in this function!
     * Note: This behaviour is subtly different from IFluidHandlers.fill()
     *
     * @param simulate If true, the insertion is only simulated
     * @param stack    the stack to insert
     * @param slot     the slot to insert to, if you don't care use OptionalInt.empty
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return ItemStack.EMPTY).
     * May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    @Nonnull
    InsertTransaction insert(@Nonnull ItemStack stack, OptionalInt slot, boolean simulate);

    /**
     * @param filter   the filter to use to extract
     * @param slot     the slot to extract from, if you don't care use OptionalInt.empty
     * @param amount   the amount to extract
     * @param simulate If true, the insertion is only simulated
     * @return ItemStack extracted from the slot, must be ItemStack.EMPTY, if nothing can be extracted
     */
    @Nonnull
    ItemStack extract(@Nonnull Predicate<ItemStack> filter, OptionalInt slot, int amount, boolean simulate);

    //i want here a bulk inset method where the return stack stacksize and the parameter stack stacksize can be greater than the normal stacksize. good idea??
    @Nonnull
    default MultiInsertTransaction bulkInsert(@Nonnull NonNullList<ItemStack> stackNonNullList, boolean simulate) {
        MultiInsertTransaction multiInsertTransaction = new MultiInsertTransaction();
        for (ItemStack stack : stackNonNullList) {
            multiInsertTransaction.addInsertTransaction(insert(stack, OptionalInt.empty(), simulate));
        }
        return multiInsertTransaction;
    }

    //i want here a bulk extract method where the return stack stacksize and the parameter stack stacksize can be greater than the normal stacksize. good idea??
    @Nonnull
    default NonNullList<ItemStack> bulkExtract(@Nonnull Predicate<ItemStack> filter, int maxItems, int maxStacks, boolean simulate) {
        NonNullList<ItemStack> itemStacklist = NonNullList.create();
        if (!canExtractFormInv()) return itemStacklist;
        int currentslot = 0;
        int stacksextracted = 0;
        int itemsextracted = 0;

        while (!(stacksextracted > maxStacks) || !(itemsextracted > maxItems || currentslot <= size())) {
            ItemStack extractedstack = extract(filter, OptionalInt.empty(), Math.min(Math.min(stacksextracted, maxItems), getStackInSlot(currentslot).getCount()), simulate);
            if (!extractedstack.isEmpty()) {
                itemsextracted += extractedstack.getCount();
                stacksextracted++;
                itemStacklist.add(extractedstack);
            }
            currentslot++;
        }
        return itemStacklist;
    }
}
