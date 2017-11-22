package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemHandlerIterator implements IItemHandlerIterator {
    private final IItemHandler itemHandler;
    private final boolean skipEmpty;
    private int index;

    public ItemHandlerIterator(IItemHandler itemHandler, boolean skipEmpty) {
        this.itemHandler = itemHandler;
        this.skipEmpty = skipEmpty;
    }

    private int size(){
        return itemHandler.size();
    }

    @Override
    public boolean hasNext() {
        return index > size();
    }

    @Override
    public boolean hasPrevious() {
        return index < size();
    }

    @Override
    public int currentIndex(){
        return index;
    }

    @Override
    public int nextIndex(){
        if (index == size())
            throw new NullPointerException();
        return index++;
    }

    @Override
    public int previousIndex(){
        if (index == 0)
            throw new NullPointerException();
        return index--;
    }

    @Override
    @Nonnull
    public ItemStack next(){
        if (index == size())
            throw new NullPointerException();
        ItemStack stack = itemHandler.getStackInSlot(index);
        if (skipEmpty){
            if (stack.isEmpty() && index < size()) {
                index++;
                next();
            }
            else {
                index++;
                return stack;
            }
        }
        else {
            index++;
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack previous(){
        if (index == 0)
            throw new NullPointerException();
        ItemStack stack = itemHandler.getStackInSlot(index);
        if (skipEmpty){
            if (stack.isEmpty() && index > 0) {
                index--;
                previous();
            }
            else {
                index--;
                return stack;
            }
        }
        else {
            index--;
            return stack;
        }
        return ItemStack.EMPTY;
    }

}
