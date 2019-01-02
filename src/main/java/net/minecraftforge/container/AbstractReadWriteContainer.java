package net.minecraftforge.container;

import net.minecraftforge.container.api.IContainerTransaction;
import net.minecraftforge.container.api.IReadWriteContainer;

/**
 * A base abstract implementation of the {@link IReadWriteContainer} interface.
 *
 * @param <T> The type stored in the container.
 */
public abstract class AbstractReadWriteContainer<T> extends ReadOnlyContainer<T> implements IReadWriteContainer<T> {

    /**
     * The current active transaction object.
     */
    protected IContainerTransaction<T> activeTransaction;

    public AbstractReadWriteContainer(final int size) {
        super(size);
    }

    public AbstractReadWriteContainer(final T... container) {
        super(container);
    }

    @Override
    public boolean isActiveTransaction(final IContainerTransaction<T> transactionToCheck) {
        return this.activeTransaction.equals(transactionToCheck);
    }
}
