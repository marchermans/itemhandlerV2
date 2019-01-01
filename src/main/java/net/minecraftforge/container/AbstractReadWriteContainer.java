package net.minecraftforge.container;

import net.minecraftforge.container.AbstractReadOnlyContainer;
import net.minecraftforge.container.api.IContainerTransaction;
import net.minecraftforge.container.api.IReadWriteContainer;
import net.minecraftforge.container.api.TransactionNotValidException;

import java.util.List;

public class AbstractReadWriteContainer<T> extends AbstractReadOnlyContainer<T> implements IReadWriteContainer<T> {


    public AbstractReadWriteContainer(T... container) {
        super(container);
    }

    @Override
    public IContainerTransaction<T> beginTransaction() {
        return null;
    }

    @Override
    public void commitTransaction(IContainerTransaction<T> transactionToCommit) throws TransactionNotValidException {

    }

    @Override
    public boolean isActiveTransaction(IContainerTransaction<T> transactionToCheck) {
        return false;
    }
}
