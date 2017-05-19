package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemViever {
    int size();

    @Nonnull
    ItemStack getStackInSlot(int slot);

    int getSlotLimit();

    default int getStackLimit(int slot, @Nonnull ItemStack stack){
        return Math.min(getSlotLimit(), stack.getMaxStackSize());
    }

    boolean isStackValid(@Nonnull ItemStack stack);
}
