package loordgek.itemhandlerv2.itemhandler;


import net.minecraft.item.ItemStack;

/**
 * Interface that represents an inventory update listener.<br/>
 * Gets notified of insertion and extraction of items in the inventories it is added to.<br/>
 *
 */
public interface IInventoryObserver {

    /**
     * Called whenever a stack is successfully inserted into an inventory.
     *
     * @param observable The inventory it was inserted into.
     * @param stack The stack that was inserted.
     */
    void onInserted(IInventoryObservable observable, ItemStack stack);

    /**
     * Called whenever a stack is successfully extracted from an inventory.
     *
     * @param observable The inventory it was extracted from.
     * @param stack The stack that was extracted.
     */
    void onExtracted(IInventoryObservable observable, ItemStack stack);
}
