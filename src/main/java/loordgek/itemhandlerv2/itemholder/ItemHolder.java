package loordgek.itemhandlerv2.itemholder;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class ItemHolder implements IItemHolder, INBTSerializable<NBTTagCompound> {
    private final NonNullList<ItemStack> stacks;

    public ItemHolder(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getSlotCount() {
        return stacks.size();
    }

    @Nonnull
    @Override
    public ItemStack getStack(int slot) {
        return stacks.get(slot);
    }

    @Override
    public boolean putStack(int slot, ItemStack stack, boolean simulated) {

        if (!simulated)
            setStack(slot, stack);
        return true;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stacks.set(slot, stack);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return ItemStackHelper.saveAllItems(new NBTTagCompound(), stacks);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        ItemStackHelper.loadAllItems(nbt, stacks);
    }
}
