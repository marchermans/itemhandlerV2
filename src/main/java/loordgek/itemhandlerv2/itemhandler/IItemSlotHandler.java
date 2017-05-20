package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IItemSlotHandler{

    ItemStack decrStackSize(int slot, int amount);

    void insertStack(@Nonnull ItemStack stack, int slot);

    boolean canTakeStack(int slot, EntityPlayer playerIn);

    boolean isStackValid(ItemStack stack);

    ItemStack getStackInSlot(int slotIndex);

    int getLimit();
}
