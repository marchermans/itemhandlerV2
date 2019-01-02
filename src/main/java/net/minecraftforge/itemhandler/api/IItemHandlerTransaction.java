package net.minecraftforge.itemhandler.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.api.IContainerTransaction;

/**
 * The default container transaction interface for ItemStacks.
 *
 * @see IContainerTransaction
 * @see ItemStack
 */
public interface IItemHandlerTransaction extends IContainerTransaction<ItemStack> {
}
