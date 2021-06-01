package net.minecraftforge.interactable.energyhandler;

import net.minecraftforge.interactable.ModifiableSlottedInteractable;
import net.minecraftforge.interactable.api.IInteractableOperationResult;
import net.minecraftforge.interactable.api.InteractableOperationResult;
import net.minecraftforge.interactable.energyhandler.api.IForgeEnergyHandlerTransaction;
import net.minecraftforge.interactable.energyhandler.api.IModifiableForgeEnergyHandler;

import java.util.Collection;

public class ModifiableForgeEnergyHandler extends ModifiableSlottedInteractable<Integer, IForgeEnergyHandlerTransaction> implements IModifiableForgeEnergyHandler {

    public ModifiableForgeEnergyHandler(int size) {
        super(size);
    }

    public ModifiableForgeEnergyHandler(Integer... iterable) {
        super(iterable);
    }

    public ModifiableForgeEnergyHandler(Collection<Integer> iterable) {
        super(iterable);
    }

    protected ForgeEnergyHandlerSlottedTransaction buildNewTransaction()
    {
        return new ForgeEnergyHandlerSlottedTransaction(this);
    }

    /**
     * The default transaction implementation.
     */
    public static class ForgeEnergyHandlerSlottedTransaction extends AbstractSlottedTransaction<Integer, IForgeEnergyHandlerTransaction> implements IForgeEnergyHandlerTransaction {

        public ForgeEnergyHandlerSlottedTransaction(ModifiableForgeEnergyHandler energyHandler) {
            super(energyHandler);
        }

        public ForgeEnergyHandlerSlottedTransaction(ForgeEnergyHandlerSlottedTransaction energyHandler) {
            super(energyHandler);
        }

        @Override
        public IInteractableOperationResult<Integer> insert(int slot, Integer toInsert) {
            //Negative or 0 power can not be inserted. They are an invalid call to this method.
            if (toInsert <= 0 || slot < 0 || slot >= size())
                return InteractableOperationResult.invalid();
            
            final Integer current = get(slot);
            final Integer newMax = current + toInsert;
            
            this.interactable.set(slot, newMax);
            super.onSlotInteracted(slot);
            
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
            super.onSlotInteracted(slot);

            return InteractableOperationResult.success(amount, newMin);
        }

        @Override
        protected ForgeEnergyHandlerSlottedTransaction buildNewTransaction()
        {
            return new ForgeEnergyHandlerSlottedTransaction(this);
        }
    }
}
