package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class Itemhandler implements IItemHandler, IItemSlotHandler, INBTSerializable<NBTTagList> {
    private final NonNullList<ItemStack> stacks;
    private final List<IItemHandlerObserver> observerList = new ArrayList<>();

    private final int size;

    public Itemhandler(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * @param stack ItemStack to check.
     * @return if the inventory can accept the stack.
     */
    @Override
    public boolean isStackValid(@Nonnull ItemStack stack) {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return stacks.get(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {

            this.stacks.set(slot, ItemStack.EMPTY);


            return existing;
        } else {
            ItemStack stack = ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract);
            this.stacks.set(slot, stack);


            return stack;
        }
    }

    @Override
    public void insertStack(@Nonnull ItemStack stack, int slot) {

        ItemStack existing = this.stacks.get(slot);

        int limit = Math.min(size(), stack.getMaxStackSize());

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))

                limit -= existing.getCount();
        }

        if (limit <= 0)
            return;

        boolean reachedLimit = stack.getCount() > limit;

        if (existing.isEmpty()) {
            this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);

        } else {
            existing.grow(reachedLimit ? limit : stack.getCount());

        }

    }

    @Nonnull
    @Override
    public ItemStack insert(@Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        if (!isStackValid(stack))
            return stack;
        ItemStack remaining = stack.copy();

        // Try to fill existing slots first
        for (ItemStack slot : stacks) {
            if (!slot.isEmpty()) {
                int max = Math.min(remaining.getMaxStackSize(), getLimit());
                int transfer = Math.min(remaining.getCount(), max - slot.getCount());
                if (transfer > 0 && ItemStack.areItemsEqual(slot, remaining) && ItemStack.areItemStackTagsEqual(slot, remaining)) {
                    slot.grow(transfer);
                    remaining.shrink(transfer);


                    if (remaining.getCount() <= 0)
                        break;

                }
            }
        }

        // Then place any remaining items in the first available empty slot
        if (remaining.getCount() > 0) {
            stacks.add(remaining);

        } else return remaining;

        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extract(Predicate<ItemStack> filter, int min, int max, boolean simulate) {
        if (max == 0)
            return ItemStack.EMPTY;
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack slot = stacks.get(i);
            if (!slot.isEmpty()) {
                int available = Math.min(max, slot.getCount());
                if (filter.test(slot) && available > 0 && slot.getCount() >= min) {
                    ItemStack pulled = slot.splitStack(available);
                    if (slot.getCount() < 1)
                        stacks.set(i, ItemStack.EMPTY);

                    return pulled;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack extractFromSlot(Predicate<ItemStack> filter, int slot, int amount, boolean simulate) {
        ItemStack stack = stacks.get(slot);
        if (!stack.isEmpty()){
            int available = Math.min(amount, stack.getCount());
            if (filter.test(stack) && available > 0){

                if (!simulate){
                    ItemStack pulled = stack.splitStack(available);
                    if (stack.isEmpty()){
                        stacks.set(slot, ItemStack.EMPTY);
                    }
                    onExtracted(slot, stack);
                    return pulled;

                }
                else {
                    ItemStack copy = stack.copy();
                    copy.setCount(available);
                    return copy;
                }

            }
        }

        return ItemStack.EMPTY;
    }


    @Override
    public int getLimit() {
        return 64;
    }

    @Override
    public boolean canTakeStack(int slot, EntityPlayer playerIn) {
        return true;
    }

    @Override
    public NBTTagList serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagList nbt) {

    }

    @Override
    public List<IItemHandlerObserver> itemObserverList() {
        return observerList;
    }
}
