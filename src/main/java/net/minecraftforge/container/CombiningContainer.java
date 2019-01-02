package net.minecraftforge.container;

import net.minecraft.util.Tuple;
import net.minecraftforge.container.api.IContainer;

import java.util.List;

public class CombiningContainer<T> implements IContainer<T> {

    protected final List<IContainer<T>> readOnlyContainers;

    public CombiningContainer(final List<IContainer<T>> readOnlyContainers) {
        this.readOnlyContainers = readOnlyContainers;
    }

    @Override
    public int getSize() {
        return readOnlyContainers.stream().mapToInt(IContainer::getSize).sum();
    }

    @Override
    public T get(int slot) {
        final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);

        return this.readOnlyContainers.get(targets.getFirst()).get(targets.getSecond());
    }

    protected Tuple<Integer, Integer> calculateInternalSlotInformationFromSlotIndex(int slotIndex)
    {
        if (slotIndex < 0 || slotIndex >= getSize())
            throw new IllegalArgumentException(String.format("Slot is not within range: 0-%d", getSize()));


        for (int i = 0; i < readOnlyContainers.size(); i++) {
            if (slotIndex < readOnlyContainers.get(i).getSize())
                return new Tuple<>(i, slotIndex);

            slotIndex -= readOnlyContainers.get(i).getSize();
        }

        throw new IllegalArgumentException(String.format("Slot is not within range: 0-%d", getSize()));
    }
}
