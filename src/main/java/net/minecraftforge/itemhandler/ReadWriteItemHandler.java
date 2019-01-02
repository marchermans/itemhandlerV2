package net.minecraftforge.itemhandler;

import net.minecraft.item.ItemStack;
import net.minecraftforge.container.AbstractReadWriteContainer;
import net.minecraftforge.container.ContainerTransactionOperationResult;
import net.minecraftforge.container.api.*;
import net.minecraftforge.itemhandler.api.IItemHandlerTransaction;
import net.minecraftforge.itemhandler.api.IReadWriteItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Arrays;
import java.util.function.Function;

/**
 * A default implementation of the {@link IReadWriteItemHandler} interface.
 * Comes with a default {@link IItemHandlerTransaction} that allows modifications to all slots.
 *
 * If a different behaviour is required during transactions, extend {@link ItemHandlerTransaction}
 * and pass a {@link Function} to create your transaction implementation when required.
 */
public class ReadWriteItemHandler extends AbstractReadWriteContainer<ItemStack> implements IReadWriteItemHandler {

    /**
     * The callback function that supplies the handler with the relevant transactions.
     */
    private final Function<ReadWriteItemHandler, ItemHandlerTransaction> transactionSupplier;

    /**
     * Creates a default handler with the given size.
     * All slots are modifyable.
     *
     * @param size The size of the container.
     */
    public ReadWriteItemHandler(int size) {
        super(size);
        this.transactionSupplier = ItemHandlerTransaction::new;
    }

    /**
     * Creates a default handler with the given array.
     * All slots are modifyable.
     *
     * @param container The container.
     */
    public ReadWriteItemHandler(ItemStack... container) {
        super(container);
        this.transactionSupplier = ItemHandlerTransaction::new;
    }

    /**
     * Creates a none default handler with the given array.
     * Modifyability of slots depends on the implementation of the
     * {@link ItemHandlerTransaction} returned by the supplier function.
     *
     * @param transactionSupplier The supplier function to generate new transactions.
     * @param size The size of the handler.
     */
    public ReadWriteItemHandler(Function<ReadWriteItemHandler, ItemHandlerTransaction> transactionSupplier, int size) {
        super(size);
        this.transactionSupplier = transactionSupplier;
    }

    /**
     * Creates a none default handler with the given size.
     * Modifyability of slots depends on the implementation of the
     * {@link ItemHandlerTransaction} returned by the supplier function.
     *
     * @param transactionSupplier The supplier function to generate new transactions.
     * @param container The container.
     */
    public ReadWriteItemHandler(Function<ReadWriteItemHandler, ItemHandlerTransaction> transactionSupplier, ItemStack... container) {
        super(container);
        this.transactionSupplier = transactionSupplier;
    }

    @Override
    public IContainerTransaction<ItemStack> beginTransaction() {
        this.activeTransaction = transactionSupplier.apply(this);
        return activeTransaction;
    }

    @Override
    public boolean isActiveTransaction(IContainerTransaction<ItemStack> transactionToCheck) {
        return false;
    }

    @Override
    public ItemStack getContentsOfSlot(int slot) {
        final ItemStack superStack = super.getContentsOfSlot(slot);

        if (superStack == null)
            return ItemStack.EMPTY;

        return superStack;
    }

    /**
     * Base implementation of the {@link IItemHandlerTransaction}.
     *
     * This is a internal class of {@link ReadWriteItemHandler} so that the underlying datastorage arrays
     * are accessible for the class, while not being accessible on the public api surface.
     *
     * If anybody has a better solution for this. Feel free to comment and/or adapt.
     */
    public class ItemHandlerTransaction extends ReadOnlyItemHandler implements IItemHandlerTransaction {

        private final ReadWriteItemHandler itemHandler;

        public ItemHandlerTransaction(ReadWriteItemHandler itemHandler) {
            super(Arrays.copyOf(itemHandler.container, itemHandler.container.length));
            this.itemHandler = itemHandler;
        }

        @Override
        public void cancel() {
            if (itemHandler.isActiveTransaction(this))
                itemHandler.activeTransaction = null;
        }

        @Override
        public void commit() throws TransactionNotValidException {
            if (!itemHandler.isActiveTransaction(this))
                throw new TransactionNotValidException(itemHandler, this);

            itemHandler.container = Arrays.copyOf(this.container, this.container.length);
        }

        @Override
        public IContainerTransactionOperationResult<ItemStack> insert(int slot, ItemStack toInsert) {
            //Empty stacks can not be inserted by default. They are an invalid call to this method.
            if (toInsert.isEmpty() || slot < 0 || slot >= getContainerSize())
                return ContainerTransactionOperationResult.invalid();

            final ItemStack stack = getContentsOfSlot(slot);
            final ItemStack secondary = stack.copy();
            final boolean stackable = ItemHandlerHelper.canItemStacksStack(stack, toInsert);

            //None stackable stacks are conflicting
            if (!stackable)
                return ContainerTransactionOperationResult.conflicting();

            final ItemStack insertedStack = stack.copy();
            insertedStack.setCount(Math.min(toInsert.getMaxStackSize(), (stack.getCount() + toInsert.getCount())));

            ItemStack primary = toInsert.copy();
            primary.setCount(toInsert.getCount() - insertedStack.getCount());
            if (primary.getCount() <= 0)
                primary = ItemStack.EMPTY;

            this.container[slot] = insertedStack;

            return ContainerTransactionOperationResult.success(primary, secondary);
        }

        @Override
        public IContainerTransactionOperationResult<ItemStack> extract(int slot, int amount) {
            //Extracting <= 0 is invalid by default for this method.
            if (amount <= 0 || slot < 0 || slot >= getContainerSize())
                return ContainerTransactionOperationResult.invalid();

            final ItemStack stack = getContentsOfSlot(slot);
            if (stack.isEmpty())
                return ContainerTransactionOperationResult.failed();

            final ItemStack extracted = stack.copy();
            extracted.setCount(Math.min(extracted.getCount(), amount));

            ItemStack remaining = stack.copy();
            remaining.setCount(remaining.getCount() - extracted.getCount());
            if (remaining.getCount() <= 0)
                remaining = ItemStack.EMPTY;

            //Clone the stack again since remaining is also a secondary output.
            this.container[slot] = remaining.copy();

            return ContainerTransactionOperationResult.success(extracted, remaining);
        }

        @Override
        public IReadWriteContainer<ItemStack> getContainer() {
            return itemHandler;
        }
    }
}
