package loordgek.itemhandlerv2.itemholder;


import loordgek.itemhandlerv2.observer.IItemHandlerObserver;
import net.minecraft.item.ItemStack;

public interface IItemHolder {

    /**
     * Gets the amount of slots in this inventory.
     */
    int getSlotCount();

    /**
     * Gets the stack in a specific slot.
     */
    ItemStack getStack(int slot);

    /**
     * Tries to set the stack in a specific slot and returns whether it was successful.
     */
    boolean putStack(int slot, ItemStack stack, boolean simulated);

    /**
     * Sets the stack in the specified slot without performing any tests.
     */
    void setStack(int slot, ItemStack stack);

    /**
     * Removes the stack in the specified slot and returns it.
     */
    default ItemStack removeStack(int slot) {
        ItemStack stack = getStack(slot);
        return putStack(slot, ItemStack.EMPTY, false) ? stack : ItemStack.EMPTY;
    }

    /**
     * Decreases the stack in the slot by the specified amount.
     */
    default ItemStack decreaseStack(int slot, int amount) {
        ItemStack newStack = getStack(slot).copy();
        ItemStack split = newStack.splitStack(amount);
        return putStack(slot, newStack, false) ? split : ItemStack.EMPTY;
    }

    /**
     * Gets the maximum stack size for a specific slot.
     */
    default int getStackSizeLimit(int slot) {
        ItemStack stack = getStack(slot);
        return stack.isEmpty() ? 64 : stack.getMaxStackSize();
    }

    /**
     * Adds an observer to this inventory that gets notified every time a stack is updated.
     */
    void addObserver(IItemHandlerObserver observer);
}
