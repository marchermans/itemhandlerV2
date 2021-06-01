package net.minecraftforge.interactable.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.interactable.combining.CombiningModifiableSlottedInteractable;
import net.minecraftforge.interactable.api.IModifiableSlottedInteractable;

import java.util.List;

public class CombiningSlottedModifiableItemHandler extends CombiningModifiableSlottedInteractable<ItemStack>
{


    public CombiningSlottedModifiableItemHandler(List<IModifiableSlottedInteractable<ItemStack, ?>> iModifiableInteractables) {
        super(iModifiableInteractables);
    }

    @Override
    protected CombiningSlottedInteractableTransaction<ItemStack> buildNewTransaction() {
        return new CombiningSlottedItemHandlerTransaction(this);
    }

    public static class CombiningSlottedItemHandlerTransaction extends CombiningSlottedInteractableTransaction<ItemStack>
    {

        public CombiningSlottedItemHandlerTransaction(CombiningModifiableSlottedInteractable<ItemStack> readWriteInteractable) {
            super(readWriteInteractable);
        }

        @Override
        protected boolean isInstancePresent(ItemStack instance) {
            return super.isInstancePresent(instance) && !instance.isEmpty();
        }

        @Override
        protected boolean didInsertGetModified(ItemStack toInsert, ItemStack remainingToInsert) {
            return remainingToInsert != null && toInsert.areCapsCompatible(remainingToInsert) && ItemStack.areItemStacksEqual(toInsert, remainingToInsert);
        }
    }
}
