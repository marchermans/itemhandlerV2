package net.minecraftforge.interactable.itemhandler.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.interactable.api.ISlottedInteractableTransaction;

/**
 * The default interactable transaction interface for ItemStacks.
 *
 * @see ISlottedInteractableTransaction
 * @see ItemStack
 */
public interface IItemHandlerTransaction extends ISlottedInteractableTransaction<ItemStack, IItemHandlerTransaction>, IItemHandler {
}
