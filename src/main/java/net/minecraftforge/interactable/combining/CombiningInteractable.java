package net.minecraftforge.interactable.combining;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.interactable.api.IInteractable;
import net.minecraftforge.interactable.api.ISlottedInteractable;
import net.minecraftforge.interactable.api.observer.IInteractableChangedHandler;
import net.minecraftforge.interactable.api.observer.IObserverWatchDog;
import net.minecraftforge.interactable.observer.ObserverWatchdog;

import java.util.*;

public class CombiningInteractable<E> implements IInteractable<E>
{
    protected final List<? extends IInteractable<E>>                   interactables;
    private final   Map<ResourceLocation, IInteractableChangedHandler<E>> observers     = new HashMap<>();
    private final List<IObserverWatchDog<E, ? extends IInteractable<E>>>                        openWatchDogs = new ArrayList<>();

    public CombiningInteractable(final List<? extends IInteractable<E>> interactables)
    {
        this.interactables = interactables;
    }

    @SafeVarargs
    public CombiningInteractable(IInteractable<E>... interactables)
    {
        this.interactables = Arrays.asList(interactables);
    }

    @Override
    public ImmutableList<E> all()
    {
        final ImmutableList.Builder<E> builder = ImmutableList.builder();
        for (final IInteractable<E> interactable :
          this.interactables)
        {
            builder.addAll(interactable.all());
        }

        return builder.build();
    }

    @Override
    public final IObserverWatchDog<E, ? extends IInteractable<E>> openObserver(ResourceLocation id, IInteractableChangedHandler<E> callback) throws IllegalArgumentException {
        if (observers.containsKey(id))
            throw new IllegalArgumentException(String.format("Observer with ID: %s is already registered.", id));

        observers.put(id, callback);

        //Add an observer to our sub handlers. That way we can passthrough any events if needed.
        if (observers.size() == 1) {
            for (final IInteractable<E> interactable :
              this.interactables) {
                openWatchDogs.add(interactable.openObserver(
                  new ResourceLocation("forge", String.format("combining%s", UUID.randomUUID())),
                  (changedInteractable) -> {
                      //Trigger observables.
                      for (final IInteractableChangedHandler<E> ownCallback :
                        observers.values()) {
                          ownCallback.onChanged(this);
                      }
                  }));
            }
        }

        return new ObserverWatchdog<E, IInteractable<E>>(this, id);
    }

    @Override
    public final void closeObserver(ResourceLocation id) throws IllegalArgumentException
    {
        if (!observers.containsKey(id))
            throw new IllegalArgumentException(String.format("Observer with ID: %s is not registered", id));

        observers.remove(id);

        if (observers.size() == 0)
        {
            for (final IObserverWatchDog<E, ? extends IInteractable<E>> watchDog :
              openWatchDogs)
            {
                watchDog.close();
            }
        }
    }
}
