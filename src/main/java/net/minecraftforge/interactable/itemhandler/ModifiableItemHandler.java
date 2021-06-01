package net.minecraftforge.interactable.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.interactable.ModifiableSlottedInteractable;
import net.minecraftforge.interactable.api.InteractableOperationResult;
import net.minecraftforge.interactable.api.*;
import net.minecraftforge.interactable.fluidhandler.api.IFluidHandlerTransaction;
import net.minecraftforge.interactable.fluidhandler.api.IModifiableFluidHandler;
import net.minecraftforge.interactable.itemhandler.api.IItemHandlerTransaction;
import net.minecraftforge.interactable.itemhandler.api.IModifiableItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Collection;

/**
 * A default implementation of the {@link IModifiableItemHandler} interface.
 * Comes with a default {@link IItemHandlerTransaction} that allows modifications to all slots.
 *
 * If a different behaviour is required during transactions, extend {@link ItemHandlerSlottedTransaction}
 * and override the {@link #beginTransaction()} method.
 */
public class ModifiableItemHandler extends ModifiableSlottedInteractable<ItemStack, IItemHandlerTransaction> implements IModifiableItemHandler
{

    /**
     * Creates a default handler with the given size.
     * All slots are modifyable.
     *
     * @param size The size of the interactable.
     */
    public ModifiableItemHandler(int size) {
        super(size);
    }

    /**
     * Creates a default handler with the given array.
     * All slots are modifyable.
     *
     * @param iterable The iterable.
     */
    public ModifiableItemHandler(ItemStack... iterable) {
        super(iterable);
    }

    /**
     * Creates a default handler with the collection as delegate.
     * All slots are modifyable.
     *
     * @param iterable The iterable.
     */
    public ModifiableItemHandler(Collection<ItemStack> iterable) {
        super(iterable);
    }

    /**
     * Method used to build a new transaction.
     * Can be overriden by subclasses to return different transactions with different behaviours.
     *
     * @return The new transaction, about to become the active transaction.
     */
    protected ItemHandlerSlottedTransaction buildNewTransaction()
    {
        return new ItemHandlerSlottedTransaction(this);
    }

    /**
     * Base implementation of the {@link IItemHandlerTransaction}.
     *
     * This is a internal class of {@link ModifiableItemHandler} so that the underlying datastorage arrays
     * are accessible for the class, while not being accessible on the public api surface.
     *
     * If anybody has a better solution for this. Feel free to comment and/or adapt.
     */
    public static class ItemHandlerSlottedTransaction extends AbstractSlottedTransaction<ItemStack, IItemHandlerTransaction> implements IItemHandlerTransaction
    {
        public ItemHandlerSlottedTransaction(ModifiableItemHandler itemHandler) {
            super(itemHandler);
        }

        public ItemHandlerSlottedTransaction(final ItemHandlerSlottedTransaction slottedTransaction)
        {
            super(slottedTransaction);

        }

        @Override
        public IInteractableOperationResult<ItemStack> insert(int slot, ItemStack toInsert) {
            //Empty stacks can not be inserted by default. They are an invalid call to this method.
            if (toInsert.isEmpty() || slot < 0 || slot >= size())
                return InteractableOperationResult.invalid();

            final ItemStack stack = get(slot);
            final ItemStack previouslyInSlot = stack.copy();
            final boolean stackable = ItemHandlerHelper.canItemStacksStack(stack, toInsert);

            //None stackable stacks are conflicting
            if (!stackable)
                return InteractableOperationResult.conflicting();

            final ItemStack insertedStack = stack.copy();
            insertedStack.setCount(Math.min(toInsert.getMaxStackSize(), (stack.getCount() + toInsert.getCount())));

            ItemStack leftOver = toInsert.copy();
            leftOver.setCount(toInsert.getCount() - insertedStack.getCount());
            if (leftOver.getCount() <= 0)
                leftOver = ItemStack.EMPTY;

            if (leftOver.getCount() == toInsert.getCount())
                return InteractableOperationResult.failed();

            this.interactable.set(slot, insertedStack);
            super.onSlotInteracted(slot);

            return InteractableOperationResult.success(leftOver, previouslyInSlot);
        }

        @Override
        public IInteractableOperationResult<ItemStack> insert(final ItemStack toInsert) {
            //Inserting an empty stack is invalid.
            if (toInsert.isEmpty())
                return InteractableOperationResult.invalid();

            boolean wasConflicted = false;
            ItemStack workingStack = toInsert.copy();
            for (int i = 0; i < size(); i++) {
                final IInteractableOperationResult<ItemStack> insertionAttemptResult = this.insert(i, workingStack);
                if (insertionAttemptResult.wasSuccessful()) {
                    workingStack = insertionAttemptResult.getPrimary();
                }
                else if (insertionAttemptResult.getStatus().isConflicting())
                {
                    wasConflicted = true;
                    if (insertionAttemptResult.getPrimary() != ItemStack.EMPTY)
                    {
                        workingStack = insertionAttemptResult.getPrimary();
                    }
                }

                if (workingStack.isEmpty())
                    return InteractableOperationResult.success(ItemStack.EMPTY, null);
            }

            if (workingStack.getCount() == toInsert.getCount())
                return InteractableOperationResult.failed();

            if (wasConflicted)
                return InteractableOperationResult.conflicting(workingStack);

            return InteractableOperationResult.success(workingStack, null);
        }

        @Override
        public IInteractableOperationResult<ItemStack> extract(int slot, int amount) {
            //Extracting <= 0 is invalid by default for this method.
            if (amount <= 0 || slot < 0 || slot >= size())
                return InteractableOperationResult.invalid();

            final ItemStack stack = get(slot);
            if (stack.isEmpty())
                return InteractableOperationResult.failed();

            final ItemStack extracted = stack.copy();
            extracted.setCount(Math.min(extracted.getCount(), amount));

            ItemStack remaining = stack.copy();
            remaining.setCount(remaining.getCount() - extracted.getCount());
            if (remaining.getCount() <= 0)
                remaining = ItemStack.EMPTY;

            //Clone the stack again since remaining is also a secondary output.
            this.interactable.set(slot, remaining.copy());
            super.onSlotInteracted(slot);

            return InteractableOperationResult.success(extracted, remaining);
        }

        @Override
        protected IItemHandlerTransaction buildNewTransaction()
        {
            return new ItemHandlerSlottedTransaction(this);
        }
    }
}
