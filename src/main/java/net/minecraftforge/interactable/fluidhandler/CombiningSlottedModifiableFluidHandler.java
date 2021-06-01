package net.minecraftforge.interactable.fluidhandler;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.interactable.combining.CombiningModifiableSlottedInteractable;
import net.minecraftforge.interactable.api.IModifiableSlottedInteractable;
import net.minecraftforge.interactable.fluidhandler.api.IFluidHandler;
import net.minecraftforge.interactable.fluidhandler.api.IFluidHandlerTransaction;
import net.minecraftforge.interactable.fluidhandler.api.IModifiableFluidHandler;

import java.util.List;

/**
 * A FluidStack specific version of the {@link CombiningModifiableSlottedInteractable} that takes care of the fact
 * that {@link FluidStack#equals(Object)} does not care for amount.
 *
 * It returns a FluidStack specific {@link CombiningSlottedInteractableTransaction} that deals with this.
 *
 * @see FluidStack
 */
public class CombiningSlottedModifiableFluidHandler extends CombiningModifiableSlottedInteractable<FluidStack> implements IFluidHandler
{

    public CombiningSlottedModifiableFluidHandler(List<IModifiableSlottedInteractable<FluidStack, ?>> iModifiableInteractables) {
        super(iModifiableInteractables);
    }

    @Override
    protected CombiningSlottedInteractableTransaction<FluidStack> buildNewTransaction() {
        return new CombiningSlottedFluidHandlerTransaction(this);
    }

    public static class CombiningSlottedFluidHandlerTransaction extends CombiningSlottedInteractableTransaction<FluidStack>
    {

        public CombiningSlottedFluidHandlerTransaction(CombiningModifiableSlottedInteractable<FluidStack> readWriteInteractable) {
            super(readWriteInteractable);
        }

        @Override
        protected boolean didInsertGetModified(FluidStack toInsert, FluidStack remainingToInsert) {
            return !toInsert.isFluidStackIdentical(remainingToInsert);
        }
    }
}
