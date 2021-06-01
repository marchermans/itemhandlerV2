package net.minecraftforge.interactable.itemhandler.searchhandlers;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.NonNullList;
import net.minecraftforge.interactable.api.IInteractableSearchHandler;

public class TagSearchHandler implements IInteractableSearchHandler<ItemStack> {

    private final ITag<Item> tag;

    public TagSearchHandler(final ITag<Item> tag) {
        this.tag = tag;
    }

    @Override
    public boolean test(ItemStack stack) {
        return tag.contains(stack.getItem());
    }

    @Override
    public ImmutableSet<ItemStack> getCandidates() {
        final NonNullList<ItemStack> candidateList = NonNullList.create();
        for (final Item item : tag.getAllElements())
        {
            final ItemGroup group = item.getGroup() != null ? item.getGroup() : ItemGroup.SEARCH;
            item.fillItemGroup(group, candidateList);
        }

        return ImmutableSet.copyOf(candidateList);
    }

    @Override
    public boolean isFullyKnown() {
        return true;
    }
}
