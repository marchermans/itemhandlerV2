package loordgek.itemhandlerv2.observer;

import loordgek.itemhandlerv2.itemhandler.IItemHandler;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface IItemHandlerObserver {

    void onInventoryChanged(IItemHandler handler, int slot, @Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, Flow flow);

    enum Flow{
        INSERT,
        EXTRACT;

        public boolean onInsert(){
            return this == INSERT;
        }

        public boolean onExtract(){
            return this == EXTRACT;
        }
    }
}
