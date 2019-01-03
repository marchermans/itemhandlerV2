package net.minecraftforge.container;

import net.minecraft.util.Tuple;
import net.minecraftforge.container.api.IContainer;

import java.util.List;

public class CombiningContainer<T> implements IContainer<T> {

    protected final List<? extends IContainer<T>> readOnlyContainers;

    public CombiningContainer(final List<? extends IContainer<T>> readOnlyContainers) {
        this.readOnlyContainers = readOnlyContainers;
    }

    @Override
    public int size() {
        int sum = 0;

        for (final IContainer<T> container :
                this.readOnlyContainers) {
            sum += container.size();
        }

        return sum;
    }

    @Override
    public T get(final int slot) {
        final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);

        return this.readOnlyContainers.get(targets.getFirst()).get(targets.getSecond());
    }

    protected Tuple<Integer, Integer> calculateInternalSlotInformationFromSlotIndex(final int slotIndex)
    {
        int workingIndex = slotIndex;
        if (workingIndex < 0 || workingIndex >= size())
            throw new IllegalArgumentException(String.format("Slot is not within range: 0-%d", size()));


        for (int i = 0; i < readOnlyContainers.size(); i++) {
            final int containerSize = readOnlyContainers.get(i).size();
            if (workingIndex < containerSize)
                return new Tuple<>(i, workingIndex);

            workingIndex -= containerSize;
        }

        throw new IllegalArgumentException(String.format("Slot is not within range: 0-%d", size()));
    }
}
