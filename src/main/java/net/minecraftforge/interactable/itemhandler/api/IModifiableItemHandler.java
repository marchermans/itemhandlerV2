package net.minecraftforge.interactable.itemhandler.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.interactable.api.IModifiableInteractable;

/**
 * The default read write interactable interface for ItemStacks.
 *
 * @see IModifiableInteractable
 * @see ItemStack
 */
public interface IModifiableItemHandler extends IModifiableInteractable<ItemStack>, IItemHandler {
}
