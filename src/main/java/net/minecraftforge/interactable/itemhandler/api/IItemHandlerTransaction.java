package net.minecraftforge.interactable.itemhandler.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.interactable.api.IInteractableTransaction;

/**
 * The default interactable transaction interface for ItemStacks.
 *
 * @see IInteractableTransaction
 * @see ItemStack
 */
public interface IItemHandlerTransaction extends IInteractableTransaction<ItemStack>, IItemHandler {
}
