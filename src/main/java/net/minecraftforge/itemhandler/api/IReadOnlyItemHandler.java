package net.minecraftforge.itemhandler.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.api.IReadOnlyContainer;

/**
 * The default readonly container interface for ItemStacks
 *
 * @see IReadOnlyContainer
 * @see ItemStack
 */
public interface IReadOnlyItemHandler extends IReadOnlyContainer<ItemStack> {
}
