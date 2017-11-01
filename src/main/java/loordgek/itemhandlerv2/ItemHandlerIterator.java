package loordgek.itemhandlerv2;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerIterator {
    private final IItemHandler itemHandler;
    private int index;

    public ItemHandlerIterator(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    public int size(){
        return itemHandler.size();
    }

    public boolean hasnext() {
        return index > size();
    }

    public boolean hasPrevious() {
        return index < size();
    }

    public int currentindex(){
        return index;
    }

    public int nextindex(){
        if (index == size())
            throw new NullPointerException();
        return index++;
    }

    public int previousindex(){
        if (index == 0)
            throw new NullPointerException();
        return index--;
    }

    @Nonnull
    public ItemStack next(){
        if (index == size())
            throw new NullPointerException();
        return itemHandler.getStackInSlot(index++);
    }

    @Nonnull
    public ItemStack previous(){
        if (index == 0)
            throw new NullPointerException();
        return itemHandler.getStackInSlot(index--);
    }

}
