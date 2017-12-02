package loordgek.itemhandlerv2.itemhandler;

import com.google.common.collect.Range;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class VoidHandler implements IItemHandler {

    public static final VoidHandler INSTANCE = new VoidHandler();

    private VoidHandler(){}

    @Override
    public int size() {
        return 100;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean canExtractStack(@Nonnull ItemStack stack) {
        return false;
    }

    @Nonnull
    @Override
    public IItemHandlerIterator itemHandlerIterator() {
        return EmptyItemHandlerItr.INSTANCE;
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
        return new InsertTransaction(stack, ItemStack.EMPTY);
    }

    @Nonnull
    @Override
    public ItemStack extract(Range<Integer> slotRange, Predicate<ItemStack> filter, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }
}
