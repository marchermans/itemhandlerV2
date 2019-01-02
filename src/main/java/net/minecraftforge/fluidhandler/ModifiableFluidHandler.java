package net.minecraftforge.fluidhandler;

import net.minecraftforge.container.api.ContainerOperationResult;
import net.minecraftforge.container.api.IContainerTransaction;
import net.minecraftforge.container.api.IContainerOperationResult;
import net.minecraftforge.container.api.IModifiableContainer;
import net.minecraftforge.container.api.TransactionNotValidException;
import net.minecraftforge.fluidhandler.api.IFluidHandlerTransaction;
import net.minecraftforge.fluidhandler.api.IModifiableFluidHandler;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.util.ListWithFixedSize;

import java.util.Collection;
import java.util.function.Function;

public class ModifiableFluidHandler extends FluidHandler implements IModifiableFluidHandler {

    /**
     * The callback function that supplies the handler with the relevant transactions.
     */
    private final Function<ModifiableFluidHandler, ModifiableFluidHandler.FluidHandlerTransaction> transactionSupplier;

    /**
     * The current transaction.
     */
    private IContainerTransaction<FluidStack> activeTransaction;

    /**
     * Creates a default handler with the given size.
     * All slots are modifyable.
     *
     * @param size The size of the container.
     */
    public ModifiableFluidHandler(int size) {
        super(size);
        this.transactionSupplier = FluidHandlerTransaction::new;
    }

    /**
     * Creates a default handler with the given array.
     * All slots are modifyable.
     *
     * @param iterable The iterable.
     */
    public ModifiableFluidHandler(FluidStack... iterable) {
        super(iterable);
        this.transactionSupplier = FluidHandlerTransaction::new;
    }

    /**
     * Creates a default handler with the collection as delegate.
     * All slots are modifyable.
     *
     * @param iterable The iterable.
     */
    public ModifiableFluidHandler(Collection<FluidStack> iterable) {
        super(iterable);
        this.transactionSupplier = FluidHandlerTransaction::new;
    }

    /**
     * Creates a none default handler with the given size.
     * Modifyability of slots depends on the implementation of the
     * {@link FluidHandlerTransaction} returned by the supplier function.
     *
     * @param transactionSupplier The supplier function to generate new transactions.
     * @param size The size of the handler.
     */
    public ModifiableFluidHandler(Function<ModifiableFluidHandler, FluidHandlerTransaction> transactionSupplier, int size) {
        super(size);
        this.transactionSupplier = transactionSupplier;
    }

    /**
     * Creates a none default handler with the given array.
     * Modifyability of slots depends on the implementation of the
     * {@link FluidHandlerTransaction} returned by the supplier function.
     *
     * @param transactionSupplier The supplier function to generate new transactions.
     * @param iterable The iterable.
     */
    public ModifiableFluidHandler(Function<ModifiableFluidHandler, FluidHandlerTransaction> transactionSupplier, FluidStack... iterable) {
        super(iterable);
        this.transactionSupplier = transactionSupplier;
    }

    /**
     * Creates a none default handler with the given collection as delegate.
     * Modifyability of slots depends on the implementation of the
     * {@link FluidHandlerTransaction} returned by the supplier function.
     *
     * @param transactionSupplier The supplier function to generate new transactions.
     * @param iterable The iterable.
     */
    public ModifiableFluidHandler(Function<ModifiableFluidHandler, FluidHandlerTransaction> transactionSupplier, Collection<FluidStack> iterable) {
        super(iterable);
        this.transactionSupplier = transactionSupplier;
    }


    @Override
    public IContainerTransaction<FluidStack> beginTransaction() {
        return transactionSupplier.apply(this);
    }

    @Override
    public boolean isActiveTransaction(IContainerTransaction<FluidStack> transactionToCheck) {
        return activeTransaction == transactionToCheck;
    }

    public class FluidHandlerTransaction extends FluidHandler implements IFluidHandlerTransaction
    {
        private final ModifiableFluidHandler fluidHandler;

        public FluidHandlerTransaction(ModifiableFluidHandler fluidHandler) {
            super(fluidHandler.container);
            this.fluidHandler = fluidHandler;
        }

        @Override
        public final void cancel() {
            if (fluidHandler.isActiveTransaction(this))
                fluidHandler.activeTransaction = null;
        }

        @Override
        public final void commit() throws TransactionNotValidException {
            if (!fluidHandler.isActiveTransaction(this))
                throw new TransactionNotValidException(fluidHandler, this);

            fluidHandler.container = new ListWithFixedSize<>(container);
        }

        @Override
        public IContainerOperationResult<FluidStack> insert(int slot, FluidStack toInsert) {
            //Null stacks can not be inserted by default. They are an invalid call to this method.
            if (toInsert == null || slot < 0 || slot >= getSize())
                return ContainerOperationResult.invalid();

            final FluidStack stack = get(slot);
            final FluidStack secondary = stack.copy();
            final boolean stackable = stack.isFluidEqual(toInsert);

            //None stackable stacks are conflicting
            if (!stackable)
                return ContainerOperationResult.conflicting();

            final FluidStack insertedStack = stack.copy();
            insertedStack.amount = stack.amount + toInsert.amount;

            FluidStack primary = toInsert.copy();
            primary.amount = toInsert.amount - insertedStack.amount;
            if (primary.amount <= 0)
                primary = null;

            this.container.set(slot, insertedStack);

            return ContainerOperationResult.success(primary, secondary);
        }

        @Override
        public IContainerOperationResult<FluidStack> extract(int slot, int amount) {
            //Extracting <= 0 is invalid by default for this method.
            if (amount <= 0 || slot < 0 || slot >= getSize())
                return ContainerOperationResult.invalid();

            final FluidStack stack = get(slot);
            if (stack == null)
                return ContainerOperationResult.failed();

            final FluidStack extracted = stack.copy();
            extracted.amount = Math.min(extracted.amount, amount);

            FluidStack remaining = stack.copy();
            remaining.amount = remaining.amount - extracted.amount;
            if (remaining.amount <= 0)
                remaining = null;

            //Clone the stack again since remaining is also a secondary output.
            this.container.set(slot, remaining != null ? remaining.copy() : null);

            return ContainerOperationResult.success(extracted, remaining);
        }

        @Override
        public IModifiableContainer<FluidStack> getContainer() {
            return fluidHandler;
        }
    }
}
