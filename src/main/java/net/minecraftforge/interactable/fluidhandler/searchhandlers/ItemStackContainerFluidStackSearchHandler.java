package net.minecraftforge.interactable.fluidhandler.searchhandlers;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.interactable.api.IInteractableSearchHandler;

public class ItemStackContainerFluidStackSearchHandler implements IInteractableSearchHandler<FluidStack> {
    private final FluidStack fluidStackToTest;
    private final MatchingStrategy matchingStrategy;

    public ItemStackContainerFluidStackSearchHandler(ItemStack itemStackToTest, MatchingStrategy matchingStrategy) {
        this.fluidStackToTest = FluidUtil.getFluidContained(itemStackToTest);
        this.matchingStrategy = matchingStrategy;
    }

    @Override
    public boolean test(FluidStack stack) {
        if (stack != null){
            switch (this.matchingStrategy)
            {
                case EXCEEDED:
                    return stack.isFluidEqual(fluidStackToTest) && fluidStackToTest.amount <= stack.amount;

                case EXACT:
                    return stack.isFluidStackIdentical(fluidStackToTest);
            }
        }
        return false;
    }

    public enum MatchingStrategy {
        /**
         * EXACT matching indicates that it will try matching exact amount of fluid.
         * Example: if the FluidIngredient asks for 500 mB water, a vanilla water bucket
         * will not be matched, as it holds 1000 mB water, 1000 != 500.
         */
        EXACT,

        /**
         * EXCEEDED matching is the default matching strategy, when not specified.
         * It will try matching those containers that can drain the specified amount of
         * fluid out. Example: if the FluidIngredient asks for 500 mB water, a vanilla bucket
         * will be matched.
         *
         */
        EXCEEDED,
    }
}
