package net.minecraftforge.util;

import java.util.*;

/**
 * A fancy array.
 * Basically a typed list that always has a given size (none used entries are null)
 * and that only supports the {@link #set(int, Object)} and {@link #get(int)} methods (like an array)
 *
 * @param <T> The type contained in the list.
 */
public class ListWithFixedSize<T> extends AbstractList<T>
        implements List<T>, RandomAccess, Cloneable, java.io.Serializable {

    private final Object[] delegate;

    /**
     * Creates a new list with the given size.
     *
     * @param size The size to create the list with.
     */
    public ListWithFixedSize(int size)
    {
        this.delegate = new Object[size];
    }

    /**
     * Creates a new list of the given array.
     *
     * @param iterable The array.
     */
    public ListWithFixedSize(T ... iterable)
    {
        this.delegate = iterable;
    }

    /**
     * Creates a new list of the given collection, by making a shallow copy.
     *
     * @param iterable The collection to shallow copy.
     */
    public ListWithFixedSize(Collection<T> iterable)
    {
        this.delegate = new ArrayList<>(iterable).toArray();
    }

    @Override
    public T get(int index) {
        return internalGet(index);
    }

    @Override
    public int size() {
        return delegate.length;
    }

    @Override
    public T set(int index, T element) {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException();

        final T previously = internalGet(index);

        delegate[index] = element;

        return previously;
    }

    @SuppressWarnings("unchecked")
    private T internalGet(int index)
    {
        return (T) delegate[index];
    }
}
