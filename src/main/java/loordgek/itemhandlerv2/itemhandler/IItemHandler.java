package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface IItemHandler extends IItemViever, IInventoryObservable {
    enum Void{
        ALLAYS,
        WHENFULL,
        NOT
    }

    /**
     * recommended is to return a {@link Collections.UnmodifiableList}.
     * @return the content from this inventory.
     */
    List<ItemStack> getContent();

    default List<ItemStack> copyContent(boolean removeEmpty){
        if (removeEmpty){
            List<ItemStack> stackList = new ArrayList<>(getContent());
            stackList.removeIf(ItemStack::isEmpty);
            return stackList;
        }
        else return new ArrayList<>(getContent());
    }

    default Void isSlotVoid(int slot){
        return Void.NOT;
    }

    //do we need to keep this ??
    /**
     * @param stack    ItemStack to check.
     * @return if a stack in the inventory can be extracted.
     */
    boolean canExtract(ItemStack stack);

    List<IInventoryObserver> getObservers();

    /**
     * Returns the ItemStack in a given slot.
     *
     * The result's stack size may be greater than the itemstacks max size.
     *
     * If the result is ItemStack.EMPTY, then the slot is empty.
     *
     * THIS WILL NOT WORK SEE https://github.com/MinecraftForge/MinecraftForge/issues/3493
     * If the result is not null but the stack size is zero, then it represents
     * an empty slot that will only accept* a specific itemstack.
     *
     * <p/>
     * IMPORTANT: This ItemStack MUST NOT be modified. This method is not for
     * altering an inventories contents. Any implementers who are able to detect
     * modification through this method should throw an exception.
     * <p/>
     * SERIOUSLY: DO NOT MODIFY THE RETURNED ITEMSTACK
     *
     * @param slot Slot to query
     * @return ItemStack in given slot. Can be ItemStack.EMPTY.
     **/
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
    ItemStack extract(@Nonnull IItemFilter filter, int min, int max, boolean simulate);

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
    default NonNullList<ItemStack> bulkExtract(@Nonnull IItemFilter filter, int min, int max, int maxstacks, boolean simulate){
        NonNullList<ItemStack> itemStacklist = NonNullList.create();
        int todo = max;
        int stacksextracted = 0;
        int itemsextracted = 0;
        while (!(stacksextracted > maxstacks) || !(itemsextracted > todo)){
            ItemStack extractedstack = extract(filter, min, max - itemsextracted, simulate);
            if (!extractedstack.isEmpty()){
                itemsextracted += extractedstack.getCount();
                stacksextracted ++;
            }
        }
        return itemStacklist;
    }
}
