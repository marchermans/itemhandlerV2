package net.minecraftforge.interactable.itemhandler.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.interactable.api.IModifiableSlottedInteractable;

/**
 * The default read write interactable interface for ItemStacks.
 *
 * @see IModifiableSlottedInteractable
 * @see ItemStack
 */
public interface IModifiableItemHandler extends IModifiableSlottedInteractable<ItemStack, IItemHandlerTransaction>, IItemHandler {
}
