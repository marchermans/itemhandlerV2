package loordgek.itemhandlerv2.itemhandler;


import com.google.common.collect.Range;
import loordgek.itemhandlerv2.filter.ItemStackFilter;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public interface IItemHandler{

    int size();

    default boolean isStackValid(ItemStack stack) {
        return true;
    }

    default boolean canExtractStack(ItemStack stack) {
        return true;
    }

    @Nonnull
    default IItemHandlerIterator itemHandlerIterator() {
        return new ItemHandlerIterator(this);
    }

    /**
     * @return A Integer in the range [0,scale] representing how "full" this inventory is.
     */
    //todo give me a better name
    default float calcRedStoneFromInventory(Range<Integer> scanRange, int scale, boolean ignoreStackSize) {
        int minSlot = (scanRange.hasLowerBound() ? scanRange.lowerEndpoint() : 0);
        int maxSlot = (scanRange.hasUpperBound() ? Math.min(scanRange.upperEndpoint(), size()) : size());

        if (ignoreStackSize){
            if (ItemHandlerHelperV2.isRangeSingleton(scanRange)){
                return getStackInSlot(scanRange.lowerEndpoint()).isEmpty() ? scale : 0;
            }
            else {
                float proportion = 0.0F;

                for (int i = minSlot; i < maxSlot; i++) {
                    if (!getStackInSlot(i).isEmpty()){
                        proportion++;
                    }
                }

                proportion = proportion / (float) size();

                return (proportion * scale);
            }
        }
        else {
            if (ItemHandlerHelperV2.isRangeSingleton(scanRange)){
                float proportion = 0.0F;

                ItemStack itemstack = getStackInSlot(scanRange.lowerEndpoint());

                if (!itemstack.isEmpty()) {
                    proportion += (float) itemstack.getCount() / (float) getStackLimit(itemstack, scanRange.lowerEndpoint());
                }

                return proportion * scale;
            }
            float proportion = 0.0F;

            for (int j = minSlot; j < maxSlot; ++j) {
                ItemStack itemstack = getStackInSlot(j);

                if (!itemstack.isEmpty()) {
                    proportion += (float) itemstack.getCount() / (float) getStackLimit(itemstack, j);
                }
            }

            proportion = proportion / (float) size();

            return (proportion * scale);
        }
    }

    int getSlotLimit(int slot);

    default int getStackLimit(ItemStack stack, int slot) {
        return Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
    }

    default int getFreeSpaceForSlot(int slot){
        ItemStack existing = getStackInSlot(slot);
        if (!existing.isEmpty()){
            if (!existing.isStackable()){
                return 0;
            }
            else return getSlotLimit(slot) - existing.getCount();
        }
        return getSlotLimit(slot);
    }

    @Nonnull
    ItemStack getStackInSlot(int slot);

    /**
     * @param slotRange
     * @param stack
     * @param simulate
     * @return
     */
    @Nonnull
    InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate);

    /**
     * @param slotRange
     * @param filter
     * @param amount
     * @param simulate
     * @return
     */
    @Nonnull
    ItemStack extract(Range<Integer> slotRange, Predicate<ItemStack> filter, int amount, boolean simulate);

    default ItemStack extractStack(Range<Integer> slotRange, @Nonnull ItemStack matchStack, boolean matchNBT, boolean matchMeta, int amount, boolean simulate){
        if (matchStack.isEmpty()) return ItemStack.EMPTY;
        ItemStackFilter.Builder filterBuilder = ItemStackFilter.filterBuilder();
        filterBuilder.withItem(matchStack.getItem());
        if (matchNBT) {
            filterBuilder.withNbtTag(matchStack.getTagCompound());
            filterBuilder.withCapNBTData(matchStack);
        }
        if (matchMeta)
            filterBuilder.withMetadata(Range.singleton(matchStack.getMetadata()));

        return extract(slotRange, filterBuilder.build(), amount, simulate);
    }
}
