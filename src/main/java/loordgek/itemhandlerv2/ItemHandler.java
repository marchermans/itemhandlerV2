package loordgek.itemhandlerv2;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;

public class ItemHandler implements IItemHandler {
    private final NonNullList<ItemStack> stacks;
    private final int size;
    private final List<IItemHandlerObserver> itemHandlerObservers = new ArrayList<>();

    public ItemHandler(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.size = size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int getSlotLimit() {
        return 64;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return stacks.get(slot);
    }

    @Nonnull
    @Override
    public InsertTransaction insert(@Nonnull ItemStack stack, OptionalInt slot, boolean simulate) {
        if (stack.isEmpty())
            return new InsertTransaction(ItemStack.EMPTY, ItemStack.EMPTY);

        if (!isStackValid(stack)) {
            return new InsertTransaction(ItemStack.EMPTY, stack);
        }

        if (slot.isPresent()) {

            int index = slot.getAsInt();

            validateSlotIndex(index);

            ItemStack existing = this.stacks.get(index);

            int limit = getStackLimit(stack);

            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                    return new InsertTransaction(ItemStack.EMPTY, stack);

                limit -= existing.getCount();
            }

            if (limit <= 0)
                return new InsertTransaction(ItemStack.EMPTY, stack);

            boolean reachedLimit = stack.getCount() > limit;


            if (!simulate) {
                if (existing.isEmpty()) {
                    this.stacks.set(index, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                    markDirty();
                    onInserted(index, existing);
                } else {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                    markDirty();
                    onInserted(index, existing);
                }

            }

            return reachedLimit ? ItemHandlerHelperV2.split(stack, stack.getCount() - limit) : new InsertTransaction(stack, ItemStack.EMPTY);
        } else {
            ItemStack copy = stack.copy();
            int limit = getStackLimit(copy);
            if (!stack.isStackable()) {

                for (int i = 0; i < size; i++) {
                    ItemStack existing = stacks.get(i);
                    if (existing.isEmpty()) {
                        if (simulate) {
                            stacks.set(i, stack);
                            markDirty();
                            onInserted(i, stacks.get(i));
                        }
                        return new InsertTransaction(stack, ItemStack.EMPTY);
                    }
                }
            } else {
                for (int i = 0; i < size; i++) {
                    ItemStack existing = stacks.get(i);

                    boolean reachedLimit = stack.getCount() > limit - existing.getCount();

                    if (!existing.isEmpty()) {
                        if (ItemHandlerHelper.canItemStacksStack(existing, copy)) {
                            if (!simulate) {
                                existing.grow(reachedLimit ? limit : copy.getCount());
                                copy.shrink(reachedLimit ? limit : copy.getCount());
                                markDirty();
                                onInserted(i, existing);
                            }
                        }
                    }
                }
                if (!copy.isEmpty()){
                    for (int i = 0; i < size; i++) {
                        ItemStack existing = stacks.get(i);
                        if (existing.isEmpty()){
                            InsertTransaction transaction = ItemHandlerHelperV2.split(stack, limit);
                            if (!simulate){
                                copy.shrink(stacks.set(i, transaction.getInsertedStack()).getCount());
                                markDirty();
                                onInserted(i, existing);
                            }
                                return transaction;
                            }
                        }
                    }
                }
            }

        return new InsertTransaction(ItemStack.EMPTY, stack);
    }


    @Nonnull
    @Override
    public ItemStack extract(Predicate<ItemStack> filter, OptionalInt slot, int amount, boolean simulate) {
        if (amount == 0 || (!canExtractFormInv()))
            return ItemStack.EMPTY;

        if (slot.isPresent()) {

            int index = slot.getAsInt();

            validateSlotIndex(index);

            ItemStack existing = this.stacks.get(index);

            if (existing.isEmpty() || (!filter.test(existing)) || (!canExtractFormInv()))
                return ItemStack.EMPTY;

            int toExtract = Math.min(amount, existing.getMaxStackSize());

            if (existing.getCount() <= toExtract) {
                if (!simulate) {
                    this.stacks.set(index, ItemStack.EMPTY);
                    onExtracted(index, existing);
                    markDirty();
                }
                return existing;
            } else {
                if (!simulate) {
                    this.stacks.set(index, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                    onExtracted(index, ItemHandlerHelper.copyStackWithSize(existing, toExtract));
                    markDirty();
                }

                return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
        } else

            for (int i = 0; i < size; i++) {
                ItemStack existing = stacks.get(i);
                if (filter.test(existing)) {

                    int toExtract = Math.min(amount, existing.getMaxStackSize());

                    if (existing.getCount() <= toExtract) {
                        if (!simulate) {
                            this.stacks.set(i, ItemStack.EMPTY);
                            onExtracted(i, existing);
                            markDirty();
                        }
                        return existing;
                    } else {
                        if (!simulate) {
                            this.stacks.set(i, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                            onExtracted(i, ItemHandlerHelper.copyStackWithSize(existing, toExtract));
                            markDirty();
                        }

                        return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
                    }
                }
            }
        return ItemStack.EMPTY;
    }

    @Override
    public List<IItemHandlerObserver> itemObserverList() {
        return itemHandlerObservers;
    }

    protected void markDirty() {
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }
}
