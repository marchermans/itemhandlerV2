package net.minecraftforge.interactable.fluidhandler;

import net.minecraftforge.interactable.ModifiableSlottedInteractable;
import net.minecraftforge.interactable.api.InteractableOperationResult;
import net.minecraftforge.interactable.api.ISlottedInteractableTransaction;
import net.minecraftforge.interactable.api.IInteractableOperationResult;
import net.minecraftforge.interactable.energyhandler.api.IForgeEnergyHandlerTransaction;
import net.minecraftforge.interactable.energyhandler.api.IModifiableForgeEnergyHandler;
import net.minecraftforge.interactable.fluidhandler.api.IFluidHandler;
import net.minecraftforge.interactable.fluidhandler.api.IFluidHandlerTransaction;
import net.minecraftforge.interactable.fluidhandler.api.IModifiableFluidHandler;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.interactable.itemhandler.api.IModifiableItemHandler;

import java.util.Collection;

public class ModifiableFluidHandler extends ModifiableSlottedInteractable<FluidStack, IFluidHandlerTransaction> implements IModifiableFluidHandler
{

    /**
     * Creates a default handler with the given size.
     * All slots are modifyable.
     *
     * @param size The size of the interactable.
     */
    public ModifiableFluidHandler(int size) {
        super(size);
    }

    /**
     * Creates a default handler with the given array.
     * All slots are modifyable.
     *
     * @param iterable The iterable.
     */
    public ModifiableFluidHandler(FluidStack... iterable) {
        super(iterable);
    }

    /**
     * Creates a default handler with the collection as delegate.
     * All slots are modifyable.
     *
     * @param iterable The iterable.
     */
    public ModifiableFluidHandler(Collection<FluidStack> iterable) {
        super(iterable);
    }

    /**
     * Method used to build a new transaction.
     * Can be overriden by subclasses to return different transactions with different behaviours.
     *
     * @return The new transaction, about to become the active transaction.
     */
    protected FluidHandlerSlottedTransaction buildNewTransaction()
    {
        return new FluidHandlerSlottedTransaction(this);
    }

    public static class FluidHandlerSlottedTransaction extends AbstractSlottedTransaction<FluidStack, IFluidHandlerTransaction> implements IFluidHandlerTransaction
    {
        public FluidHandlerSlottedTransaction(ModifiableFluidHandler fluidHandler) {
            super(fluidHandler);
        }

        public FluidHandlerSlottedTransaction(final FluidHandlerSlottedTransaction transaction)
        {
            super(transaction);
        }

        @Override
        public IInteractableOperationResult<FluidStack> insert(int slot, FluidStack toInsert) {
            //Null stacks can not be inserted by default. They are an invalid call to this method.
            if (toInsert == null || slot < 0 || slot >= size())
                return InteractableOperationResult.invalid();

            final FluidStack stack = get(slot);
            final FluidStack secondary = stack.copy();
            final boolean stackable = stack.isFluidEqual(toInsert);

            //None stackable stacks are conflicting
            if (!stackable)
                return InteractableOperationResult.conflicting();

            final FluidStack insertedStack = stack.copy();
            insertedStack.setAmount(stack.getAmount() + toInsert.getAmount());

            FluidStack primary = toInsert.copy();
            primary.setAmount(toInsert.getAmount() - insertedStack.getAmount());
            if (primary.getAmount() <= 0)
                primary = null;

            this.interactable.set(slot, insertedStack);
            super.onSlotInteracted(slot);

            return InteractableOperationResult.success(primary, secondary);
        }

        @Override
        public IInteractableOperationResult<FluidStack> insert(FluidStack toInsert) {
            //Inserting an empty stack is invalid.
            if (toInsert == null)
                return InteractableOperationResult.invalid();

            boolean wasConflicted = false;
            FluidStack workingStack = toInsert.copy();
            for (int i = 0; i < size(); i++) {
                final IInteractableOperationResult<FluidStack> insertionAttemptResult = this.insert(i, workingStack);
                if (insertionAttemptResult.wasSuccessful()) {
                    workingStack = insertionAttemptResult.getPrimary();
                } else if (insertionAttemptResult.getStatus().isConflicting())
                {
                    wasConflicted = true;
                }

                if (workingStack == null)
                    return InteractableOperationResult.success(null, null);
            }

            if (wasConflicted)
                return InteractableOperationResult.conflicting();

            if (workingStack.getAmount() == toInsert.getAmount())
                return InteractableOperationResult.failed();

            return InteractableOperationResult.success(workingStack, null);
        }

        @Override
        public IInteractableOperationResult<FluidStack> extract(int slot, int amount) {
            //Extracting <= 0 is invalid by default for this method.
            if (amount <= 0 || slot < 0 || slot >= size())
                return InteractableOperationResult.invalid();

            final FluidStack stack = get(slot);
            if (stack == null)
                return InteractableOperationResult.failed();

            final FluidStack extracted = stack.copy();
            extracted.setAmount(Math.min(extracted.getAmount(), amount));

            FluidStack remaining = stack.copy();
            remaining.setAmount(remaining.getAmount() - extracted.getAmount());
            if (remaining.getAmount() <= 0)
                remaining = null;

            //Clone the stack again since remaining is also a secondary output.
            this.interactable.set(slot, remaining != null ? remaining.copy() : null);
            this.onSlotInteracted(slot);

            return InteractableOperationResult.success(extracted, remaining);
        }

        @Override
        protected IFluidHandlerTransaction buildNewTransaction()
        {
            return new FluidHandlerSlottedTransaction(this);
        }
    }
}
