package loordgek.itemhandlerv2.itemhandler;

import com.google.common.collect.Range;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class EmptyHandler implements IItemHandler {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public int getSlotLimit() {
        return 0;
    }

    @Override
    public boolean isStackValid(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean canExtractStack(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public IItemHandlerIterator itemHandlerIterator(boolean skipEmpty) {
        return new EmptyItemHandlerItr();
    }

    @Override
    public float calcRedStoneFromInventory(Range<Integer> scanRange, int scale, boolean ignoreStackSize) {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate) {
        return new InsertTransaction(ItemStack.EMPTY, stack);
    }

    @Nonnull
    @Override
    public ItemStack extract(Range<Integer> slotRange, Predicate<ItemStack> filter, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }
}
