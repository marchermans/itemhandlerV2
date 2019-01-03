package net.minecraftforge.interactable;

import net.minecraft.util.Tuple;
import net.minecraftforge.interactable.api.IInteractable;

import java.util.List;

public class CombiningInteractable<T> implements IInteractable<T> {

    protected final List<? extends IInteractable<T>> readOnlyInteractables;

    public CombiningInteractable(final List<? extends IInteractable<T>> readOnlyInteractables) {
        this.readOnlyInteractables = readOnlyInteractables;
    }

    @Override
    public int size() {
        int sum = 0;

        for (final IInteractable<T> interactable :
                this.readOnlyInteractables) {
            sum += interactable.size();
        }

        return sum;
    }

    @Override
    public T get(final int slot) {
        final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);

        return this.readOnlyInteractables.get(targets.getFirst()).get(targets.getSecond());
    }

    protected Tuple<Integer, Integer> calculateInternalSlotInformationFromSlotIndex(final int slotIndex)
    {
        int workingIndex = slotIndex;
        if (workingIndex < 0 || workingIndex >= size())
            throw new IllegalArgumentException(String.format("Slot is not within range: 0-%d", size()));


        for (int i = 0; i < readOnlyInteractables.size(); i++) {
            final int interactableSize = readOnlyInteractables.get(i).size();
            if (workingIndex < interactableSize)
                return new Tuple<>(i, workingIndex);

            workingIndex -= interactableSize;
        }

        throw new IllegalArgumentException(String.format("Slot is not within range: 0-%d", size()));
    }
}
