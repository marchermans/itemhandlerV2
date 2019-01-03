package net.minecraftforge.interactable;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.Tuple;
import net.minecraftforge.interactable.api.IInteractable;
import net.minecraftforge.interactable.api.IModifiableInteractable;

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

    @Override
    public ImmutableList<T> all() {
        final ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (final IInteractable<T> interactable :
                this.readOnlyInteractables) {
            builder.addAll(interactable.all());
        }

        return builder.build();
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

    protected Integer calculateSlotIndexFromInternalSlotInfomation(final IInteractable<T> interactable, final int inInteractableSlotIndex)
    {
        return getStartIndexOrSubInteractable(interactable) + inInteractableSlotIndex;
    }

    protected Integer getStartIndexOrSubInteractable(final IInteractable<T> interactable)
    {
        int workingIndexOffSet = 0;
        for (final IInteractable<T> i :
                this.readOnlyInteractables) {
            if (i == interactable)
                return workingIndexOffSet;

            workingIndexOffSet += i.size();
        }

        throw new IllegalArgumentException("Can not find interactable in this combining interactable.");
    }
}
