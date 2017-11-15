package loordgek.itemhandlerv2.wrappers;

import net.minecraft.inventory.IInventory;

public class InvWrapper extends IInvWrapperBase {

    private final IInventory inventory;

    public InvWrapper(IInventory inventory) {
        this.inventory = inventory;
    }



    protected IInventory getInventory() {
        return inventory;
    }
}
