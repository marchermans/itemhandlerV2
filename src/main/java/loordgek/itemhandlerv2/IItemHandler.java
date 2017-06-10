package loordgek.itemhandlerv2;


import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;

public interface IItemHandler extends Iterable<ItemStack>, IItemHandlerObservable {

    int size();

    default int containsItems(ItemStack stack){
        int items = 0;
        Iterator<ItemStack> stackIterator = iterator();
        while (stackIterator.hasNext()){
            ItemStack itrstack = iterator().next();
            if(ItemHandlerHelper.canItemStacksStack(itrstack, stack)){
                items += itrstack.getCount();
            }
        }
        return items;
    }

    boolean isStackValid(ItemStack stack);

    List<ItemStack> getContent();

    /**
     *
     * @return A Integer in the range [0,scale] representing how "full" this inventory is.
     */
    //todo give me a better name
    int calcRedstoneFromInventory(int scale);

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
     *
     * @param filter
     * @param slot the slot to extract from, if you don't care use OptionalInt.empty
     * @param amount the amount to extract
     * @param simulate If true, the insertion is only simulated
     * @return
     */
    @Nonnull
    ItemStack extract(Predicate<ItemStack> filter, OptionalInt slot, int amount, boolean simulate);

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
            ItemStack extractedstack = extract(filter, OptionalInt.empty(), Math.min(Math.min(stacksextracted, max), getStackInSlot(currentslot).getCount()), simulate);
            if (!extractedstack.isEmpty()){
                itemsextracted += extractedstack.getCount();
                stacksextracted ++;
                itemStacklist.add(extractedstack);
            }
            currentslot++;
        }
        return itemStacklist;
    }
}
