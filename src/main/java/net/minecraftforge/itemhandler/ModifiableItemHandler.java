package net.minecraftforge.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.Container;
import net.minecraftforge.container.api.ContainerOperationResult;
import net.minecraftforge.container.api.*;
import net.minecraftforge.itemhandler.api.IItemHandlerTransaction;
import net.minecraftforge.itemhandler.api.IModifiableItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.util.ListWithFixedSize;

import java.util.Collection;
import java.util.function.Function;

/**
 * A default implementation of the {@link IModifiableItemHandler} interface.
 * Comes with a default {@link IItemHandlerTransaction} that allows modifications to all slots.
 *
 * If a different behaviour is required during transactions, extend {@link ItemHandlerTransaction}
 * and pass a {@link Function} to create your transaction implementation when required.
 */
public class ModifiableItemHandler extends ItemHandler implements IModifiableItemHandler {

    /**
     * The current transaction.
     */
    private IContainerTransaction<ItemStack> activeTransaction;

    /**
     * Creates a default handler with the given size.
     * All slots are modifyable.
     *
     * @param size The size of the container.
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

    @Override
    public IContainerTransaction<ItemStack> beginTransaction() {
        this.activeTransaction = new ItemHandlerTransaction(this);
        return activeTransaction;
    }

    @Override
    public boolean isActiveTransaction(IContainerTransaction<ItemStack> transactionToCheck) {
        return activeTransaction == transactionToCheck;
    }

    /**
     * Base implementation of the {@link IItemHandlerTransaction}.
     *
     * This is a internal class of {@link ModifiableItemHandler} so that the underlying datastorage arrays
     * are accessible for the class, while not being accessible on the public api surface.
     *
     * If anybody has a better solution for this. Feel free to comment and/or adapt.
     */
    public class ItemHandlerTransaction extends ItemHandler implements IItemHandlerTransaction {

        private final ModifiableItemHandler itemHandler;

        public ItemHandlerTransaction(ModifiableItemHandler itemHandler) {
            super(itemHandler.container);
            this.itemHandler = itemHandler;
        }

        @Override
        public final void cancel() {
            if (itemHandler.isActiveTransaction(this))
                itemHandler.activeTransaction = null;
        }

        @Override
        public final void commit() throws TransactionNotValidException {
            if (!itemHandler.isActiveTransaction(this))
                throw new TransactionNotValidException(itemHandler, this);

            itemHandler.container = new ListWithFixedSize<>(container);
        }

        @Override
        public IContainerOperationResult<ItemStack> insert(int slot, ItemStack toInsert) {
            //Empty stacks can not be inserted by default. They are an invalid call to this method.
            if (toInsert.isEmpty() || slot < 0 || slot >= size())
                return ContainerOperationResult.invalid();

            final ItemStack stack = get(slot);
            final ItemStack previouslyInSlot = stack.copy();
            final boolean stackable = ItemHandlerHelper.canItemStacksStack(stack, toInsert);

            //None stackable stacks are conflicting
            if (!stackable)
                return ContainerOperationResult.conflicting();

            final ItemStack insertedStack = stack.copy();
            insertedStack.setCount(Math.min(toInsert.getMaxStackSize(), (stack.getCount() + toInsert.getCount())));

            ItemStack leftOver = toInsert.copy();
            leftOver.setCount(toInsert.getCount() - insertedStack.getCount());
            if (leftOver.getCount() <= 0)
                leftOver = ItemStack.EMPTY;

            if (leftOver.getCount() == toInsert.getCount())
                return ContainerOperationResult.failed();

            this.container.set(slot, insertedStack);

            return ContainerOperationResult.success(leftOver, previouslyInSlot);
        }

        @Override
        public IContainerOperationResult<ItemStack> insert(final ItemStack toInsert) {
            //Inserting an empty stack is invalid.
            if (toInsert.isEmpty())
                return ContainerOperationResult.invalid();

            boolean wasConflicted = false;
            ItemStack workingStack = toInsert.copy();
            for (int i = 0; i < size(); i++) {
                final IContainerOperationResult<ItemStack> insertionAttemptResult = this.insert(i, workingStack);
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
                    return ContainerOperationResult.success(ItemStack.EMPTY, null);
            }

            if (workingStack.getCount() == toInsert.getCount())
                return ContainerOperationResult.failed();

            if (wasConflicted)
                return ContainerOperationResult.conflicting(workingStack);

            return ContainerOperationResult.success(workingStack, null);
        }

        @Override
        public IContainerOperationResult<ItemStack> extract(int slot, int amount) {
            //Extracting <= 0 is invalid by default for this method.
            if (amount <= 0 || slot < 0 || slot >= size())
                return ContainerOperationResult.invalid();

            final ItemStack stack = get(slot);
            if (stack.isEmpty())
                return ContainerOperationResult.failed();

            final ItemStack extracted = stack.copy();
            extracted.setCount(Math.min(extracted.getCount(), amount));

            ItemStack remaining = stack.copy();
            remaining.setCount(remaining.getCount() - extracted.getCount());
            if (remaining.getCount() <= 0)
                remaining = ItemStack.EMPTY;

            //Clone the stack again since remaining is also a secondary output.
            this.container.set(slot, remaining.copy());

            return ContainerOperationResult.success(extracted, remaining);
        }

        @Override
        public IModifiableContainer<ItemStack> getContainer() {
            return itemHandler;
        }
    }
}
