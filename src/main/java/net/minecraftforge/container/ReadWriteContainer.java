package net.minecraftforge.container;

import net.minecraftforge.container.api.IContainerTransaction;
import net.minecraftforge.container.api.IReadWriteContainer;
import net.minecraftforge.container.api.TransactionNotValidException;

public class ReadWriteContainer<T> extends ReadOnlyContainer<T> implements IReadWriteContainer<T> {

    private IContainerTransaction<T> activeTransaction;

    public ReadWriteContainer(final int size) {
        super(size);
    }

    public ReadWriteContainer(final T... container) {
        super(container);
    }

    @Override
    public IContainerTransaction<T> beginTransaction() {
        return null;
    }

    @Override
    public void commitTransaction(final IContainerTransaction<T> transactionToCommit) throws TransactionNotValidException {
        if (!isActiveTransaction(transactionToCommit))
            throw new TransactionNotValidException(this, transactionToCommit);

        //TODO: Commit.
    }

    @Override
    public boolean isActiveTransaction(final IContainerTransaction<T> transactionToCheck) {
        return this.activeTransaction.equals(transactionToCheck);
    }
}
