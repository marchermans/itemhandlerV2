package net.minecraftforge.interactable.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.interactable.Interactable;
import net.minecraftforge.interactable.api.IInteractable;
import net.minecraftforge.interactable.itemhandler.api.IItemHandler;

import java.util.Collection;

/**
 * The default implementation of {@link IItemHandler} interface.
 *
 * @see Interactable
 * @see IInteractable
 * @see IItemHandler
 */
public class ItemHandler extends Interactable<ItemStack> implements IItemHandler {

    public ItemHandler(int size) {
        super(size);
    }

    public ItemHandler(ItemStack... iterable) {
        super(iterable);
    }

    public ItemHandler(Collection<ItemStack> iterable)
    {
        super(iterable);
    }

    @Override
    public ItemStack get(int slot) {
        final ItemStack superStack = super.get(slot);

        if (superStack == null)
            return ItemStack.EMPTY;

        return superStack;
    }
}
