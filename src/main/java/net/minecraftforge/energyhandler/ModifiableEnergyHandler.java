package net.minecraftforge.energyhandler;

import net.minecraftforge.container.api.ContainerOperationResult;
import net.minecraftforge.container.api.IContainerTransaction;
import net.minecraftforge.container.api.IContainerOperationResult;
import net.minecraftforge.container.api.IModifiableContainer;
import net.minecraftforge.container.api.TransactionNotValidException;
import net.minecraftforge.energyhandler.api.IEnergyHandlerTransaction;
import net.minecraftforge.energyhandler.api.IModifiableEnergyHandler;
import net.minecraftforge.util.ListWithFixedSize;

import java.util.Collection;
import java.util.function.Function;

public class ModifiableEnergyHandler extends EnergyHandler implements IModifiableEnergyHandler {

    /**
     * The callback function that supplies the handler with the relevant transactions.
     */
    private final Function<ModifiableEnergyHandler, EnergyHandlerTransaction> transactionSupplier;

    /**
     * The current transaction.
     */
    private IContainerTransaction<Integer> activeTransaction;

    /**
     * Creates a default handler with the given size.
     * All slots are modifyable.
     *
     * @param size The size of the container.
     */
    public ModifiableEnergyHandler(int size) {
        super(size);
        this.transactionSupplier = EnergyHandlerTransaction::new;
    }

    /**
     * Creates a default handler with the given array.
     * All slots are modifyable.
     *
     * @param iterable The iterable.
     */
    public ModifiableEnergyHandler(Integer... iterable) {
        super(iterable);
        this.transactionSupplier = EnergyHandlerTransaction::new;
    }

    /**
     * Creates a default handler with the collection as delegate.
     * All slots are modifyable.
     *
     * @param iterable The iterable.
     */
    public ModifiableEnergyHandler(Collection<Integer> iterable) {
        super(iterable);
        this.transactionSupplier = EnergyHandlerTransaction::new;
    }

    /**
     * Creates a none default handler with the given size.
     * Modifyability of slots depends on the implementation of the
     * {@link EnergyHandlerTransaction} returned by the supplier function.
     *
     * @param transactionSupplier The supplier function to generate new transactions.
     * @param size The size of the handler.
     */
    public ModifiableEnergyHandler(Function<ModifiableEnergyHandler, EnergyHandlerTransaction> transactionSupplier, int size) {
        super(size);
        this.transactionSupplier = transactionSupplier;
    }

    /**
     * Creates a none default handler with the given array.
     * Modifyability of slots depends on the implementation of the
     * {@link EnergyHandlerTransaction} returned by the supplier function.
     *
     * @param transactionSupplier The supplier function to generate new transactions.
     * @param iterable The iterable.
     */
    public ModifiableEnergyHandler(Function<ModifiableEnergyHandler, EnergyHandlerTransaction> transactionSupplier, Integer... iterable) {
        super(iterable);
        this.transactionSupplier = transactionSupplier;
    }

    /**
     * Creates a none default handler with the given collection as delegate.
     * Modifyability of slots depends on the implementation of the
     * {@link EnergyHandlerTransaction} returned by the supplier function.
     *
     * @param transactionSupplier The supplier function to generate new transactions.
     * @param iterable The iterable.
     */
    public ModifiableEnergyHandler(Function<ModifiableEnergyHandler, EnergyHandlerTransaction> transactionSupplier, Collection<Integer> iterable) {
        super(iterable);
        this.transactionSupplier = transactionSupplier;
    }



    @Override
    public IContainerTransaction<Integer> beginTransaction() {
        return transactionSupplier.apply(this);
    }

    @Override
    public boolean isActiveTransaction(IContainerTransaction<Integer> transactionToCheck) {
        return activeTransaction == transactionToCheck;
    }

    public class EnergyHandlerTransaction extends EnergyHandler implements IEnergyHandlerTransaction {

        private final ModifiableEnergyHandler energyHandler;

        public EnergyHandlerTransaction(ModifiableEnergyHandler energyHandler) {
            super(energyHandler.container);
            this.energyHandler = energyHandler;
        }

        @Override
        public void cancel() {
            if (energyHandler.isActiveTransaction(this))
                energyHandler.activeTransaction = null;
        }

        @Override
        public void commit() throws TransactionNotValidException {
            if (!energyHandler.isActiveTransaction(this))
                throw new TransactionNotValidException(energyHandler, this);

            energyHandler.container = new ListWithFixedSize<>(this.container);
        }

        @Override
        public IContainerOperationResult<Integer> insert(int slot, Integer toInsert) {
            //Negative or 0 power can not be inserted. They are an invalid call to this method.
            if (toInsert <= 0 || slot < 0 || slot >= size())
                return ContainerOperationResult.invalid();
            
            final Integer current = get(slot);
            final Integer newMax = current + toInsert;
            
            this.container.set(slot, newMax);
            
            return ContainerOperationResult.success(0, current);
        }

        @Override
        public IContainerOperationResult<Integer> insert(Integer toInsert) {
            //Inserting an empty stack is invalid.
            if (toInsert <= 0)
                return ContainerOperationResult.invalid();

            boolean wasConflicted = false;
            int workingCount = toInsert;
            for (int i = 0; i < size(); i++) {
                final IContainerOperationResult<Integer> insertionAttemptResult = this.insert(i, workingCount);
                if (insertionAttemptResult.wasSuccessful()) {
                    workingCount = insertionAttemptResult.getPrimary();
                } else if (insertionAttemptResult.getStatus().isConflicting())
                {
                    wasConflicted = true;
                }

                if (workingCount <= 0)
                    return ContainerOperationResult.success(null, null);
            }

            if (wasConflicted)
                return ContainerOperationResult.conflicting();

            if (workingCount == toInsert)
                return ContainerOperationResult.failed();

            return ContainerOperationResult.success(workingCount, null);
        }

        @Override
        public IContainerOperationResult<Integer> extract(int slot, int amount) {
            //Negative or 0 power can not be extracted. They are an invalid call to this method.
            if (amount <= 0 || slot < 0 || slot >= size())
                return ContainerOperationResult.invalid();

            final Integer current = get(slot);
            final Integer newMin = Math.min(0, current - amount);
            
            this.container.set(slot, newMin);

            return ContainerOperationResult.success(amount, newMin);
        }

        @Override
        public IModifiableContainer<Integer> getContainer() {
            return this.energyHandler;
        }
    }
}
