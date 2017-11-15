package loordgek.itemhandlerv2.wrappers;

import com.google.common.collect.Range;
import loordgek.itemhandlerv2.itemhandler.IItemHandler;
import loordgek.itemhandlerv2.itemhandler.InsertTransaction;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class RangedWrapperTODO implements IItemHandler {
    private final IItemHandler compose;
    private final int min;
    private final int max;

    public RangedWrapperTODO(IItemHandler compose, int min, int max) {
        this.compose = compose;
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isStackValid(@Nonnull ItemStack stack) {
        return compose.isStackValid(stack);
    }

    @Override
    public boolean canExtractStack(@Nonnull ItemStack stack) {
        return compose.canExtractStack(stack);
    }

    @Override
    public int size() {
        return max - min;
    }

    @Override
    public int getSlotLimit() {
        return compose.getSlotLimit();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return compose.getStackInSlot(slot + min);
    }

    @Nonnull
    @Override
    public InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate) {
        return compose.insert(Range.closed(min, max), stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extract(Range<Integer> slotRange, Predicate<ItemStack> filter, int amount, boolean simulate) {
        return compose.extract(Range.closed(min, max), filter, amount, simulate);
    }
}
