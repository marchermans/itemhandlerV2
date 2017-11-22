package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class EmptyItemHandlerItr implements IItemHandlerIterator {
    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    @Override
    public int currentIndex() {
        return 0;
    }

    @Override
    public int nextIndex() {
        return 0;
    }

    @Override
    public int previousIndex() {
        return 0;
    }

    @Nonnull
    @Override
    public ItemStack next() {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack previous() {
        return ItemStack.EMPTY;
    }
}
