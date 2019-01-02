package net.minecraftforge.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.ReadOnlyContainer;
import net.minecraftforge.itemhandler.api.IReadOnlyItemHandler;

/**
 * The default implementation of {@link IReadOnlyItemHandler} interface.
 *
 * @see ReadOnlyContainer
 * @see net.minecraftforge.container.api.IReadOnlyContainer
 * @see net.minecraftforge.itemhandler.api.IReadOnlyItemHandler
 */
public class ReadOnlyItemHandler extends ReadOnlyContainer<ItemStack> implements IReadOnlyItemHandler {

    public ReadOnlyItemHandler(int size) {
        super(size);
    }

    public ReadOnlyItemHandler(ItemStack... container) {
        super(container);
    }

    protected ReadOnlyItemHandler(Object... container) {
        super(container);
    }

    @Override
    public ItemStack getContentsOfSlot(int slot) {
        final ItemStack superStack = super.getContentsOfSlot(slot);

        if (superStack == null)
            return ItemStack.EMPTY;

        return superStack;
    }
}
