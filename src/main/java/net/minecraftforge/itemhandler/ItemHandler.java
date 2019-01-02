package net.minecraftforge.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.Container;
import net.minecraftforge.container.api.IContainer;
import net.minecraftforge.itemhandler.api.IItemHandler;

import java.util.Collection;

/**
 * The default implementation of {@link IItemHandler} interface.
 *
 * @see Container
 * @see IContainer
 * @see IItemHandler
 */
public class ItemHandler extends Container<ItemStack> implements IItemHandler {

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
