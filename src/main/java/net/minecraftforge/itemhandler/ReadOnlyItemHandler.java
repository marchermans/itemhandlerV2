package net.minecraftforge.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.AbstractReadOnlyContainer;
import net.minecraftforge.itemhandler.api.IReadOnlyItemHandler;

/**
 * The default implementation of {@link IReadOnlyItemHandler} interface.
 *
 * Using the {@link AbstractReadOnlyContainer#makeImmutable(Object)} implementation used in this class
 * it is ensured that instances returned by its {@link AbstractReadOnlyContainer#getContentsOfSlot(int)}
 * method are either a copy of the stack, by calling {@link ItemStack#copy()} or {@link ItemStack#EMPTY}
 * is returned when null (meaning nothing is stored in the slot) is requisted.
 *
 * @see net.minecraftforge.container.AbstractReadOnlyContainer
 * @see net.minecraftforge.container.api.IReadOnlyContainer
 * @see net.minecraftforge.itemhandler.api.IReadOnlyItemHandler
 */
public class ReadOnlyItemHandler extends AbstractReadOnlyContainer<ItemStack> implements IReadOnlyItemHandler {

    public ReadOnlyItemHandler(int size) {
        super(size);
    }

    public ReadOnlyItemHandler(ItemStack... container) {
        super(container);
    }

    @Override
    protected ItemStack makeImmutable(ItemStack tInstance) {
        if (tInstance == null)
            return ItemStack.EMPTY;

        return tInstance.copy();
    }
}
