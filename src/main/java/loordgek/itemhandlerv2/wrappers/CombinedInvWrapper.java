package loordgek.itemhandlerv2.wrappers;

import com.google.common.collect.Range;
import loordgek.itemhandlerv2.itemhandler.IItemHandler;
import loordgek.itemhandlerv2.itemhandler.InsertTransaction;
import loordgek.itemhandlerv2.itemhandler.ItemHandlerHelperV2;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class CombinedInvWrapper implements IItemHandler {
    protected final IItemHandler[] handlers;
    protected final int[] baseIndex; // index-offsets of the different handlers
    protected final int slotCount; // number of total slots

    public CombinedInvWrapper(IItemHandler... handlers) {
        this.handlers = handlers;
        this.baseIndex = new int[handlers.length];
        int index = 0;
        for (int i = 0; i < handlers.length; i++)
        {
            index += handlers[i].size();
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }

    // returns the handler index for the slot
    protected int getIndexForSlot(int slot)
    {
        if (slot < 0)
            throw new IndexOutOfBoundsException();

        for (int i = 0; i < baseIndex.length; i++)
        {
            if (slot - baseIndex[i] < 0)
            {
                return i;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    protected IItemHandler getHandlerFromIndex(int index)
    {
        if (index < 0 || index >= handlers.length)
        {
            throw new IndexOutOfBoundsException();
        }
        return handlers[index];
    }

    protected int getSlotFromIndex(int slot, int index)
    {
        if (index <= 0 || index >= baseIndex.length)
        {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    public float calcRedStoneFromInventory(Range<Integer> scanRange, int scale, boolean ignoreStackSize) {
        if (ignoreStackSize){
            float proportion = 0.0F;

            for (int i = 0; i < size(); i++) {
                if (!getStackInSlot(i).isEmpty()){
                    proportion++;
                }
            }

            proportion = proportion / (float) size();

            return (proportion * scale);
        }
        else {
            float proportion = 0.0F;

            for (int j = 0; j < size(); ++j) {
                int index = getIndexForSlot(j);
                IItemHandler handler = getHandlerFromIndex(index);
                int slot = getSlotFromIndex(j, index);
                ItemStack itemstack = handler.getStackInSlot(slot);

                if (!itemstack.isEmpty()) {
                    proportion += (float) itemstack.getCount() / (float) handler.getStackLimit(itemstack);
                }
            }

            proportion = proportion / (float) size();

            return (proportion * scale);
        }
    }

    @Override
    public boolean isStackValid(@Nonnull ItemStack stack) {
        for (IItemHandler handler : handlers){
            if (handler.isStackValid(stack)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canExtractStack(@Nonnull ItemStack stack) {
        for (IItemHandler handler : handlers){
            if (handler.canExtractStack(stack)){
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return slotCount;
    }

    @Override
    public int getSlotLimit() {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        int index = getIndexForSlot(slot);
        IItemHandler handler = getHandlerFromIndex(index);
        int slotindex = getSlotFromIndex(slot, index);
        return handler.getStackInSlot(slotindex);
    }

    @Nonnull
    @Override
    public InsertTransaction insert(Range<Integer> slotRange, ItemStack stack, boolean simulate) {
        if (ItemHandlerHelperV2.isRangeSlotLess(slotRange)){
            for (IItemHandler handler : handlers){
                InsertTransaction transaction = handler.insert(Range.all(), stack, simulate);
                if (!transaction.getInsertedStack().isEmpty())
                    return transaction;
            }
        }
        else if (ItemHandlerHelperV2.isRangeSingleton(slotRange)){
            int slot = slotRange.lowerEndpoint();
            int index = getIndexForSlot(slot);
            IItemHandler handler = getHandlerFromIndex(index);
            return handler.insert(Range.singleton(getSlotFromIndex(slot, index)), stack, simulate);
        }
        else {
            int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
            int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
            int minIndex = getIndexForSlot(minSlot);
            int maxIndex = getIndexForSlot(maxSlot);
            int currentMinSlot = minSlot;
            for (int i = minIndex; i < maxIndex; i++) {

                IItemHandler handler = getHandlerFromIndex(i);
                if (maxSlot >= handler.size() && currentMinSlot == 0){
                    InsertTransaction Inserted = handler.insert(Range.all(), stack, simulate);
                    if (!Inserted.getInsertedStack().isEmpty()){
                        return Inserted;
                    }
                    else currentMinSlot -= handler.size();
                }
                else {
                    int currentMaxSlot = Math.min(handler.size(), minSlot);
                    InsertTransaction Inserted = handler.insert(Range.closed(currentMinSlot, currentMaxSlot), stack, simulate);
                    if (!Inserted.getInsertedStack().isEmpty()){
                        return Inserted;
                    }
                    else currentMinSlot -= (currentMaxSlot - currentMinSlot);
                }
            }
        }
        return new InsertTransaction(ItemStack.EMPTY, stack);
    }

    @Nonnull
    @Override
    public ItemStack extract(Range<Integer> slotRange, Predicate<ItemStack> filter, int amount, boolean simulate) {
        if (ItemHandlerHelperV2.isRangeSlotLess(slotRange)){
            for (IItemHandler handler : handlers){
                ItemStack extract = handler.extract(Range.all(), filter, amount, simulate);
                if (!extract.isEmpty())
                    return extract;
            }
        }
        else if (ItemHandlerHelperV2.isRangeSingleton(slotRange)){
            int slot = slotRange.lowerEndpoint();
            int index = getIndexForSlot(slot);
            IItemHandler handler = getHandlerFromIndex(index);
            return handler.extract(Range.singleton(getSlotFromIndex(slot, index)), filter, amount, simulate);
        }
        else {
            int minSlot = (slotRange.hasLowerBound() ? slotRange.lowerEndpoint() : 0);
            int maxSlot = (slotRange.hasUpperBound() ? Math.min(slotRange.upperEndpoint(), size()) : size());
            int minIndex = getIndexForSlot(minSlot);
            int maxIndex = getIndexForSlot(maxSlot);
            int currentMinSlot = minSlot;
            for (int i = minIndex; i < maxIndex; i++) {

                IItemHandler handler = getHandlerFromIndex(i);
                if (maxSlot >= handler.size() && currentMinSlot == 0){
                   ItemStack extracted = handler.extract(Range.all(), filter, amount, simulate);
                   if (!extracted.isEmpty()){
                       return extracted;
                   }
                   else currentMinSlot -= handler.size();
                }
                else {
                    int currentMaxSlot = Math.min(handler.size(), maxSlot);
                    ItemStack extracted = handler.extract(Range.closed(currentMinSlot, currentMaxSlot), filter, amount, simulate);
                    if (!extracted.isEmpty()){
                        return extracted;
                    }
                    else currentMinSlot -= (currentMaxSlot - currentMinSlot);
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
