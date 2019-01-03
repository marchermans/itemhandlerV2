package net.minecraftforge.interactable.fluidhandler;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.interactable.CombiningModifiableInteractable;
import net.minecraftforge.interactable.api.IModifiableInteractable;

import java.util.List;

/**
 * A FluidStack specific version of the {@link CombiningModifiableInteractable} that takes care of the fact
 * that {@link FluidStack#equals(Object)} does not care for amount.
 *
 * It returns a FluidStack specific {@link CombiningInteractableTransaction} that deals with this.
 *
 * @see FluidStack
 */
public class CombiningModifiableFluidHandler extends CombiningModifiableInteractable<FluidStack> {

    public CombiningModifiableFluidHandler(List<IModifiableInteractable<FluidStack>> iModifiableInteractables) {
        super(iModifiableInteractables);
    }

    @Override
    protected CombiningInteractableTransaction<FluidStack> buildNewTransaction() {
        return new CombiningFluidHandlerTransaction(this);
    }

    public class CombiningFluidHandlerTransaction extends CombiningInteractableTransaction<FluidStack>
    {

        public CombiningFluidHandlerTransaction(CombiningModifiableInteractable<FluidStack> readWriteInteractable) {
            super(readWriteInteractable);
        }

        @Override
        protected boolean didInsertGetModified(FluidStack toInsert, FluidStack remainingToInsert) {
            return !toInsert.isFluidStackIdentical(remainingToInsert);
        }
    }
}
