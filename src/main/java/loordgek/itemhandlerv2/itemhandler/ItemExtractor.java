package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class ItemExtractor implements IItemHandlerObserver {
    private final IItemHandler itemHandler;
    private final Predicate<ItemStack> filter;
    private int index;
    private int lastRet = -1;

    public ItemExtractor(IItemHandler itemHandler, Predicate<ItemStack> filter, int index) {
        this.itemHandler = itemHandler;
        this.filter = filter;
        this.index = index;
        lastRet += index;
    }

    public boolean hasNext(){
        return index != size();
    }

    public boolean hasPrevious(){
        return lastRet > 0;
    }

    @Nonnull
    public ItemStack next(){
        int i = index;
        ItemStack stack = itemHandler.getStackInSlot(index);
        index = i + 1;
        return stack;
    }

    @Nonnull
    public ItemStack previous(){
        int i = index - 1;
        ItemStack stack = itemHandler.getStackInSlot(index);
        lastRet = index = i;
        return stack;

    }

    private int size(){
        return itemHandler.size();
    }

    @Override
    public void onInserted(IItemHandlerObservable observable, int slot, ItemStack stack) {

    }

    @Override
    public void onExtracted(IItemHandlerObservable observable, int slot, ItemStack stack) {

    }
}
