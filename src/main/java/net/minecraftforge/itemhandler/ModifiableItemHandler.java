package net.minecraftforge.itemhandler;

import net.minecraft.item.ItemStack;
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
     * The callback function that supplies the handler with the relevant transactions.
     */
    private final Function<ModifiableItemHandler, ItemHandlerTransaction> transactionSupplier;

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
        this.transactionSupplier = ItemHandlerTransaction::new;
    }

    /**
     * Creates a default handler with the given array.
     * All slots are modifyable.
     *
     * @param iterable The iterable.
     */
    public ModifiableItemHandler(ItemStack... iterable) {
        super(iterable);
        this.transactionSupplier = ItemHandlerTransaction::new;
    }

    /**
     * Creates a default handler with the collection as delegate.
     * All slots are modifyable.
     *
     * @param iterable The iterable.
     */
    public ModifiableItemHandler(Collection<ItemStack> iterable) {
        super(iterable);
        this.transactionSupplier = ItemHandlerTransaction::new;
    }

    /**
     * Creates a none default handler with the given size.
     * Modifyability of slots depends on the implementation of the
     * {@link ItemHandlerTransaction} returned by the supplier function.
     *
     * @param transactionSupplier The supplier function to generate new transactions.
     * @param size The size of the handler.
     */
    public ModifiableItemHandler(Function<ModifiableItemHandler, ItemHandlerTransaction> transactionSupplier, int size) {
        super(size);
        this.transactionSupplier = transactionSupplier;
    }

    /**
     * Creates a none default handler with the given array.
     * Modifyability of slots depends on the implementation of the
     * {@link ItemHandlerTransaction} returned by the supplier function.
     *
     * @param transactionSupplier The supplier function to generate new transactions.
     * @param iterable The iterable.
     */
    public ModifiableItemHandler(Function<ModifiableItemHandler, ItemHandlerTransaction> transactionSupplier, ItemStack... iterable) {
        super(iterable);
        this.transactionSupplier = transactionSupplier;
    }

    /**
     * Creates a none default handler with the given collection as delegate.
     * Modifyability of slots depends on the implementation of the
     * {@link ItemHandlerTransaction} returned by the supplier function.
     *
     * @param transactionSupplier The supplier function to generate new transactions.
     * @param iterable The iterable.
     */
    public ModifiableItemHandler(Function<ModifiableItemHandler, ItemHandlerTransaction> transactionSupplier, Collection<ItemStack> iterable) {
        super(iterable);
        this.transactionSupplier = transactionSupplier;
    }

    @Override
    public IContainerTransaction<ItemStack> beginTransaction() {
        this.activeTransaction = transactionSupplier.apply(this);
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
            if (toInsert.isEmpty() || slot < 0 || slot >= getSize())
                return ContainerOperationResult.invalid();

            final ItemStack stack = get(slot);
            final ItemStack secondary = stack.copy();
            final boolean stackable = ItemHandlerHelper.canItemStacksStack(stack, toInsert);

            //None stackable stacks are conflicting
            if (!stackable)
                return ContainerOperationResult.conflicting();

            final ItemStack insertedStack = stack.copy();
            insertedStack.setCount(Math.min(toInsert.getMaxStackSize(), (stack.getCount() + toInsert.getCount())));

            ItemStack primary = toInsert.copy();
            primary.setCount(toInsert.getCount() - insertedStack.getCount());
            if (primary.getCount() <= 0)
                primary = ItemStack.EMPTY;

            if (primary.getCount() == toInsert.getCount())
                return ContainerOperationResult.failed();

            this.container.set(slot, insertedStack);

            return ContainerOperationResult.success(primary, secondary);
        }

        @Override
        public IContainerOperationResult<ItemStack> extract(int slot, int amount) {
            //Extracting <= 0 is invalid by default for this method.
            if (amount <= 0 || slot < 0 || slot >= getSize())
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
