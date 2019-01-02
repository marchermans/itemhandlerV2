package net.minecraftforge.itemhandler.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.api.IContainer;

/**
 * The default readonly container interface for ItemStacks
 *
 * @see IContainer
 * @see ItemStack
 */
public interface IItemHandler extends IContainer<ItemStack> {
}
