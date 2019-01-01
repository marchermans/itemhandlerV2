package net.minecraftforge.container.api;

public interface IContainerTransaction<T> extends IReadOnlyContainer<T> {

    void cancel();

    void commit();

    IContainerTransactionOperationResult<T> insert(int slot, T toInsert);

    IContainerTransactionOperationResult<T> extract(int slot, int amount);
}
