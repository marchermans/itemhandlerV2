package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public interface IItemHandler extends Iterable<ItemStack>, IItemHandlerObservable{
    enum Void{
        ALLAYS,
        WHENFULL,
        NOT
    }

    int size();

    default Void isSlotVoid(int slot){
        return Void.NOT;
    }

    default int containsItems(ItemStack stack){
        int items = 0;
        while (iterator().hasNext()){
            ItemStack itrstack = iterator().next();
            if(ItemHandlerHelper.canItemStacksStack(itrstack, stack)){
                items += itrstack.getCount();
            }
        }
        return items;
    }

    boolean isStackValid(ItemStack stack);

    int getLimit();

    @Nonnull
    ItemStack getStackInSlot(int slot);

    /**
     * Inserts an ItemStack into the given slot and return the remainder.
     * The ItemStack should not be modified in this function!
     * Note: This behaviour is subtly different from IFluidHandlers.fill()
     *
     * @param simulate If true, the insertion is only simulated
     * @return The remaining ItemStack that was not inserted (if the entire stack is accepted, then return ItemStack.EMPTY).
     *         May be the same as the input ItemStack if unchanged, otherwise a new ItemStack.
     **/
    @Nonnull
    ItemStack insert(@Nonnull ItemStack stack, boolean simulate);

    /**
     * Extracts an ItemStack from the given slot. The returned value must be ItemStack.EMPTY
     * if nothing is extracted, otherwise it's stack size must not be greater than amount or the
     * itemstacks getMaxStackSize().
     *
     * @param simulate If true, the extraction is only simulated
     * @return ItemStack extracted from the slot, must be ItemStack.EMPTY, if nothing can be extracted
     **/
    @Nonnull
    ItemStack extract(Predicate<ItemStack> filter, int min, int max, boolean simulate);

    @Nonnull
    ItemStack extractFromSlot(Predicate<ItemStack> filter, int slot, int amount, boolean simulate);

    //i want here a bulk inset method where the return stack stacksize and the parameter stack stacksize can be greater than the normal stacksize. good idea??
    @Nonnull
    default NonNullList<ItemStack> bulkInsert(@Nonnull NonNullList<ItemStack> stackNonNullList, boolean simulate){
        NonNullList<ItemStack> itemStacklist = NonNullList.create();
        for (ItemStack stack : stackNonNullList){
            itemStacklist.add(insert(stack, simulate));
        }
        return itemStacklist;
    }

    //i want here a bulk extract method where the return stack stacksize and the parameter stack stacksize can be greater than the normal stacksize. good idea??
    @Nonnull
    default NonNullList<ItemStack> bulkExtract(Predicate<ItemStack> filter, int min, int max, int maxstacks, boolean simulate){
        NonNullList<ItemStack> itemStacklist = NonNullList.create();
        int currentslot = 0;
        int stacksextracted = 0;
        int itemsextracted = 0;
        while (!(stacksextracted > maxstacks) || !(itemsextracted > max || currentslot <= size())){
            ItemStack extractedstack = extract(filter, min, max - itemsextracted, simulate);
            if (!extractedstack.isEmpty()){
                itemsextracted += extractedstack.getCount();
                stacksextracted ++;
                itemStacklist.add(extractedstack);
            }
            currentslot++;
        }
        return itemStacklist;
    }

    default ItemExtractor getItemExtractor(Predicate<ItemStack> filter, int slot){
        ItemExtractor extractor = new ItemExtractor(this, filter, slot);
        addObserver(extractor);
        return extractor;

    }
}
