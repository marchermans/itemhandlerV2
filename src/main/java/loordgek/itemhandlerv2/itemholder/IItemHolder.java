package loordgek.itemhandlerv2.itemholder;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemHolder {

    default boolean isStackValidForSlot(@Nonnull ItemStack stack, int slot) {
        return true;
    }

    default boolean canExtractStackFormSlot(@Nonnull ItemStack stack, int slot) {
        return true;
    }
    /**
     * Gets the amount of slots in this inventory.
     */
    int getSlotCount();

    /**
     * Gets the stack in a specific slot.
     */
    @Nonnull
    ItemStack getStack(int slot);

    /**
     * Tries to set the stack in a specific slot and returns whether it was successful.
     */
    boolean putStack(int slot, ItemStack stack, boolean simulate);

    /**
     * Sets the stack in the specified slot without performing any tests.
     */
    void setStack(int slot, ItemStack stack);

    /**
     * Removes the stack in the specified slot and returns it.
     */
    @Nonnull
    default ItemStack removeStack(int slot) {
        ItemStack stack = getStack(slot);
        return putStack(slot, ItemStack.EMPTY, false) ? stack : ItemStack.EMPTY;
    }

    /**
     * Decreases the stack in the slot by the specified amount.
     */
    @Nonnull
    default ItemStack decreaseStack(int slot, int amount, boolean simulate) {
        ItemStack newStack = getStack(slot).copy();
        ItemStack split = newStack.splitStack(amount);
        return putStack(slot, newStack, simulate) ? split : ItemStack.EMPTY;
    }

    /**
     * Gets the maximum stack size for a specific slot.
     */
    default int getStackSizeLimit(int slot) {
        ItemStack stack = getStack(slot);
        return stack.isEmpty() ? 64 : stack.getMaxStackSize();
    }
}
