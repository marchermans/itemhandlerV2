package net.minecraftforge.itemhandler.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.api.IModifiableContainer;

/**
 * The default read write container interface for ItemStacks.
 *
 * @see IModifiableContainer
 * @see ItemStack
 */
public interface IModifiableItemHandler extends IModifiableContainer<ItemStack>, IItemHandler {
}
