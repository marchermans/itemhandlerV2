package net.minecraftforge.container;

import com.google.common.collect.Lists;
import net.minecraftforge.container.api.IReadOnlyContainer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AbstractReadOnlyContainer<T> implements IReadOnlyContainer<T> {

    //Stupid java generics T[] does not exist.
    private Object[] container;

    public AbstractReadOnlyContainer(final int size)
    {
        this.container = new Object[size];
    }

    public AbstractReadOnlyContainer(final T ... container) {
        this.container = container;
    }

    @Override
    public int getContainerSize() {
        return container.length;
    }

    @Override
    public T getContentsOfSlot(final int slot) {
        return (T) container[slot];
    }

    protected void setContainer(Object[] container) {
        this.container = container;
    }
}
