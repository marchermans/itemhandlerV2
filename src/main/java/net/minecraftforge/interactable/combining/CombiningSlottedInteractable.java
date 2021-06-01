package net.minecraftforge.interactable.combining;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.interactable.api.IModifiableSlottedInteractable;
import net.minecraftforge.interactable.api.ISlottedInteractable;
import net.minecraftforge.interactable.api.observer.IObserverWatchDog;
import net.minecraftforge.interactable.api.observer.ISlottedInteractableChangedHandler;
import net.minecraftforge.interactable.observer.ObserverWatchdog;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class CombiningSlottedInteractable<E> implements ISlottedInteractable<E>
{

    private final Map<ResourceLocation, ISlottedInteractableChangedHandler<E>>  observers     = new HashMap<>();
    protected final List<? extends ISlottedInteractable<E>>                       readOnlySlottedInteractables;
    private final List<IObserverWatchDog<E, ? extends ISlottedInteractable<E>>> openWatchDogs = new ArrayList<>();

    public CombiningSlottedInteractable(final List<? extends ISlottedInteractable<E>> readOnlySlottedInteractables)
    {
        this.readOnlySlottedInteractables = readOnlySlottedInteractables;
    }

    @Override
    public int size()
    {
        int sum = 0;

        for (final ISlottedInteractable<E> interactable :
          this.readOnlySlottedInteractables)
        {
            sum += interactable.size();
        }

        return sum;
    }

    @Override
    public E get(final int slot)
    {
        final Tuple<Integer, Integer> targets = calculateInternalSlotInformationFromSlotIndex(slot);

        return this.readOnlySlottedInteractables.get(targets.getA()).get(targets.getB());
    }

    @Override
    public ImmutableList<E> all()
    {
        final ImmutableList.Builder<E> builder = ImmutableList.builder();
        for (final ISlottedInteractable<E> interactable :
          this.readOnlySlottedInteractables)
        {
            builder.addAll(interactable.all());
        }

        return builder.build();
    }

    @Override
    public IObserverWatchDog<E, ? extends ISlottedInteractable<E>> openObserver(
      final ResourceLocation id, final ISlottedInteractableChangedHandler<E> callback) throws IllegalArgumentException
    {
        if (observers.containsKey(id))
        {
            throw new IllegalArgumentException(String.format("Observer with ID: %s is already registered.", id));
        }

        observers.put(id, callback);

        //Add an observer to our sub handlers. That way we can passthrough any events if needed.
        if (observers.size() == 1)
        {
            for (final ISlottedInteractable<E> interactable :
              this.readOnlySlottedInteractables)
            {
                openWatchDogs.add(interactable.openObserver(
                  new ResourceLocation("forge", String.format("combining%s", UUID.randomUUID())),
                  (changedInteractable, slots) -> {
                      final int interactableOffset = getStartIndexOrSubInteractable(changedInteractable);
                      final Set<Integer> remappedSlots = slots.stream().map(i -> i + interactableOffset).collect(Collectors.toSet());

                      //Trigger observables.
                      for (final ISlottedInteractableChangedHandler<E> ownCallback :
                        observers.values())
                      {
                          ownCallback.onChanged(this, remappedSlots);
                      }
                  }));
            }
        }

        return new ObserverWatchdog<>(this, id);
    }

    @Override
    public void closeObserver(final ResourceLocation id) throws IllegalArgumentException
    {
        if (!observers.containsKey(id))
        {
            throw new IllegalArgumentException(String.format("Observer with ID: %s is not registered", id));
        }

        observers.remove(id);

        if (observers.size() == 0)
        {
            for (final IObserverWatchDog<E, ? extends ISlottedInteractable<E>> watchDog :
              openWatchDogs)
            {
                watchDog.close();
            }
        }
    }

    protected Tuple<Integer, Integer> calculateInternalSlotInformationFromSlotIndex(final int slotIndex)
    {
        int workingIndex = slotIndex;
        if (workingIndex < 0 || workingIndex >= size())
        {
            throw new IllegalArgumentException(String.format("Slot is not within range: 0-%d", size()));
        }


        for (int i = 0; i < readOnlySlottedInteractables.size(); i++)
        {
            final int interactableSize = readOnlySlottedInteractables.get(i).size();
            if (workingIndex < interactableSize)
            {
                return new Tuple<>(i, workingIndex);
            }

            workingIndex -= interactableSize;
        }

        throw new IllegalArgumentException(String.format("Slot is not within range: 0-%d", size()));
    }

    Integer getStartIndexOrSubInteractable(final ISlottedInteractable<E> interactable)
    {
        int workingIndexOffSet = 0;
        for (final ISlottedInteractable<E> i :
          this.readOnlySlottedInteractables)
        {
            if (i == interactable)
            {
                return workingIndexOffSet;
            }

            workingIndexOffSet += i.size();
        }

        throw new IllegalArgumentException("Can not find interactable in this combining interactable.");
    }
}
