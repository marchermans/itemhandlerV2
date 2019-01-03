package net.minecraftforge.interactable.itemhandler;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.interactable.CombiningInteractable;
import net.minecraftforge.interactable.CombiningModifiableInteractable;
import net.minecraftforge.interactable.CombiningModifiableInteractable.CombiningInteractableTransaction;
import net.minecraftforge.interactable.api.IInteractable;
import net.minecraftforge.interactable.api.IInteractableTransaction;
import net.minecraftforge.interactable.api.IModifiableInteractable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

public class CombiningModifiableItemHandler extends CombiningModifiableInteractable<ItemStack> {


    public CombiningModifiableItemHandler(List<IModifiableInteractable<ItemStack>> iModifiableInteractables) {
        super(iModifiableInteractables);
    }

    @Override
    protected CombiningInteractableTransaction<ItemStack> buildNewTransaction() {
        return new CombiningItemHandlerTransaction(this);
    }

    public class CombiningItemHandlerTransaction extends CombiningInteractableTransaction<ItemStack> {

        public CombiningItemHandlerTransaction(CombiningModifiableInteractable<ItemStack> readWriteInteractable) {
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
