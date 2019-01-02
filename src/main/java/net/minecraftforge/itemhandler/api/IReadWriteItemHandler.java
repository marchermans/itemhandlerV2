package net.minecraftforge.itemhandler.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.api.IReadWriteContainer;

/**
 * The default read write container interface for ItemStacks.
 *
 * @see IReadWriteContainer
 * @see ItemStack
 */
public interface IReadWriteItemHandler extends IReadWriteContainer<ItemStack>, IReadOnlyItemHandler {
}
