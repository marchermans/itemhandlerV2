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
    public int getContainerSize() {
        return readOnlyContainers.stream().mapToInt(IContainer::getContainerSize).sum();
    }

    @Override
    public T getContentsOfSlot(int slot) {
        final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);

        return this.readOnlyContainers.get(targets.getFirst()).getContentsOfSlot(targets.getSecond());
    }

    protected Tuple<Integer, Integer> calculateInternalSlotInformationFromSlotIndex(int slotIndex)
    {
        if (slotIndex < 0 || slotIndex >= getContainerSize())
            throw new IllegalArgumentException(String.format("Slot is not within range: 0-%d", getContainerSize()));


        for (int i = 0; i < readOnlyContainers.size(); i++) {
            if (slotIndex < readOnlyContainers.get(i).getContainerSize())
                return new Tuple<>(i, slotIndex);

            slotIndex -= readOnlyContainers.get(i).getContainerSize();
        }

        throw new IllegalArgumentException(String.format("Slot is not within range: 0-%d", getContainerSize()));
    }
}
