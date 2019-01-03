package net.minecraftforge.interactable.energyhandler;

import net.minecraftforge.interactable.api.InteractableOperationResult;
import net.minecraftforge.interactable.api.IInteractableTransaction;
import net.minecraftforge.interactable.api.IInteractableOperationResult;
import net.minecraftforge.interactable.api.IModifiableInteractable;
import net.minecraftforge.interactable.api.TransactionNotValidException;
import net.minecraftforge.interactable.energyhandler.api.IForgeEnergyHandlerTransaction;
import net.minecraftforge.interactable.energyhandler.api.IModifiableForgeEnergyHandler;
import net.minecraftforge.util.ListWithFixedSize;

import java.util.Collection;
import java.util.function.Function;

public class ModifiableForgeEnergyHandler extends ForgeEnergyHandler implements IModifiableForgeEnergyHandler {

    /**
     * The current transaction.
     */
    private IInteractableTransaction<Integer> activeTransaction;

    public ModifiableForgeEnergyHandler(int size) {
        super(size);
    }

    public ModifiableForgeEnergyHandler(Integer... iterable) {
        super(iterable);
    }

    public ModifiableForgeEnergyHandler(Collection<Integer> iterable) {
        super(iterable);
    }

    @Override
    public final IInteractableTransaction<Integer> beginTransaction() {
        this.activeTransaction = buildNewTransaction();
        return this.activeTransaction;
    }

    /**
     * Method used to build a new transaction.
     * Can be overriden by subclasses to return different transactions with different behaviours.
     *
     * @return The new transaction, about to become the active transaction.
     */
    protected ForgeEnergyHandlerTransaction buildNewTransaction()
    {
        return new ForgeEnergyHandlerTransaction(this);
    }

    @Override
    public final boolean isActiveTransaction(IInteractableTransaction<Integer> transactionToCheck) {
        return activeTransaction == transactionToCheck;
    }

    /**
     * The default transaction implementation.
     */
    public class ForgeEnergyHandlerTransaction extends ForgeEnergyHandler implements IForgeEnergyHandlerTransaction {

        private final ModifiableForgeEnergyHandler energyHandler;

        public ForgeEnergyHandlerTransaction(ModifiableForgeEnergyHandler energyHandler) {
            super(energyHandler.interactable);
            this.energyHandler = energyHandler;
        }

        @Override
        public final void cancel() {
            if (energyHandler.isActiveTransaction(this))
                energyHandler.activeTransaction = null;
        }

        @Override
        public final void commit() throws TransactionNotValidException {
            if (!energyHandler.isActiveTransaction(this))
                throw new TransactionNotValidException(energyHandler, this);

            energyHandler.interactable = new ListWithFixedSize<>(this.interactable);
        }

        @Override
        public IInteractableOperationResult<Integer> insert(int slot, Integer toInsert) {
            //Negative or 0 power can not be inserted. They are an invalid call to this method.
            if (toInsert <= 0 || slot < 0 || slot >= size())
                return InteractableOperationResult.invalid();
            
            final Integer current = get(slot);
            final Integer newMax = current + toInsert;
            
            this.interactable.set(slot, newMax);
            
            return InteractableOperationResult.success(0, current);
        }

        @Override
        public IInteractableOperationResult<Integer> insert(Integer toInsert) {
            //Inserting an empty stack is invalid.
            if (toInsert <= 0)
                return InteractableOperationResult.invalid();

            boolean wasConflicted = false;
            int workingCount = toInsert;
            for (int i = 0; i < size(); i++) {
                final IInteractableOperationResult<Integer> insertionAttemptResult = this.insert(i, workingCount);
                if (insertionAttemptResult.wasSuccessful()) {
                    workingCount = insertionAttemptResult.getPrimary();
                } else if (insertionAttemptResult.getStatus().isConflicting())
                {
                    wasConflicted = true;
                }

                if (workingCount <= 0)
                    return InteractableOperationResult.success(null, null);
            }

            if (wasConflicted)
                return InteractableOperationResult.conflicting();

            if (workingCount == toInsert)
                return InteractableOperationResult.failed();

            return InteractableOperationResult.success(workingCount, null);
        }

        @Override
        public IInteractableOperationResult<Integer> extract(int slot, int amount) {
            //Negative or 0 power can not be extracted. They are an invalid call to this method.
            if (amount <= 0 || slot < 0 || slot >= size())
                return InteractableOperationResult.invalid();

            final Integer current = get(slot);
            final Integer newMin = Math.min(0, current - amount);
            
            this.interactable.set(slot, newMin);

            return InteractableOperationResult.success(amount, newMin);
        }

        @Override
        public final IModifiableInteractable<Integer> getInteractable() {
            return this.energyHandler;
        }
    }
}
