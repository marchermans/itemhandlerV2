package net.minecraftforge.interactable.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.interactable.SlottedInteractable;
import net.minecraftforge.interactable.api.ISlottedInteractable;
import net.minecraftforge.interactable.itemhandler.api.IItemHandler;

import java.util.Collection;

/**
 * The default implementation of {@link IItemHandler} interface.
 *
 * @see SlottedInteractable
 * @see ISlottedInteractable
 * @see IItemHandler
 */
public class ItemHandler extends SlottedInteractable<ItemStack> implements IItemHandler {

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
