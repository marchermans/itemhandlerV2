package net.minecraftforge.interactable.itemhandler.searchhandlers;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.interactable.api.IInteractableSearchHandler;

public class ExactItemStackSearchHandler implements IInteractableSearchHandler<ItemStack> {

    private final ItemStack stack;

    public ExactItemStackSearchHandler(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean test(ItemStack stack) {
        return ItemStack.areItemStacksEqual(this.stack, stack) && this.stack.areCapsCompatible(stack);
    }

    @Override
    public ImmutableSet<ItemStack> getCandidates() {
        return ImmutableSet.of(stack);
    }

    @Override
    public boolean isFullyKnown() {
        return true;
    }
}
