package loordgek.itemhandlerv2.itemhandler;

import loordgek.itemhandlerv2.test.TestMod;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerIterator implements IItemHandlerIterator {
    private final IItemHandler itemHandler;
    private int index = 0;

    public ItemHandlerIterator(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    private int size() {
        return itemHandler.size();
    }

    @Override
    public boolean hasNext() {
        return index != size();
    }

    @Override
    public boolean hasPrevious() {
        return index != 0;
    }

    @Override
    public int currentIndex() {
        return index;
    }

    @Override
    public int nextIndex() {
        if (index == size())
            throw new NullPointerException();
        return index + 1;
    }

    @Override
    public int previousIndex() {
        if (index == 0)
            throw new NullPointerException();
        return index - 1;
    }

    @Override
    @Nonnull
    public ItemStack next() {
        ItemStack stack = itemHandler.getStackInSlot(index);
        index++;
        return stack;

    }

    @Override
    @Nonnull
    public ItemStack previous() {
        ItemStack stack = itemHandler.getStackInSlot(index);
        index--;
        return stack;
    }
}
