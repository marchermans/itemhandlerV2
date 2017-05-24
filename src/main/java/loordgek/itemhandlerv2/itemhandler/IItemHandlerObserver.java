package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;

public interface IItemHandlerObserver {
    /**
     * Called whenever a stack is successfully inserted into an inventory.
     *
     * @param observable The inventory it was inserted into.
     * @param slot the slot it was inserted into
     * @param stack The stack that was inserted.
     */
    void onInserted(IItemHandlerObservable observable, int slot, ItemStack stack);

    /**
     * Called whenever a stack is successfully extracted from an inventory.
     *
     * @param observable The inventory it was extracted from.
     * @param slot the slot it was extracted from
     * @param stack The stack that was extracted.
     */
    void onExtracted(IItemHandlerObservable observable, int slot, ItemStack stack);
}
