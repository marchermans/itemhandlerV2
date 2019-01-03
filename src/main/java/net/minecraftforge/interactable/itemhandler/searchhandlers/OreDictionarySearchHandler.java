package net.minecraftforge.interactable.itemhandler.searchhandlers;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.ItemStack;
import net.minecraftforge.interactable.api.IInteractableSearchHandler;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionarySearchHandler implements IInteractableSearchHandler<ItemStack> {

    private final ImmutableSet<ItemStack> stacks;

    public OreDictionarySearchHandler(final String oreId) {
        this.stacks = ImmutableSet.copyOf(OreDictionary.getOres(oreId));
    }

    @Override
    public boolean test(ItemStack stack) {
        for (final ItemStack candidate :
              stacks) {
            if (ItemStack.areItemStacksEqual(candidate, stack) && candidate.areCapsCompatible(stack))
                return true;
        }

        return false;
    }

    @Override
    public ImmutableSet<ItemStack> getCandidates() {
        return stacks;
    }

    @Override
    public boolean isFullyKnown() {
        return true;
    }
}
