package net.minecraftforge.interactable.api;

import com.google.common.collect.ImmutableSet;

import java.util.function.Predicate;

/**
 * Represents a predicate with (if known ahead of time) possible candidates that could match.
 * This allows indexed interactable to check for the candidates first, before iterating over
 * the interactable.
 *
 * This indexing functionality is not used by default, but allows other implementations to use
 * this kind of speedup.
 *
 * @param <T> The type of the interactable
 */
@FunctionalInterface
public interface IInteractableSearchHandler<T> extends Predicate<T> {

    @Override
    boolean test(T t);

    /**
     * Possible candidates that can be used to search indexed inventories
     * quickly before falling back to predicate search.
     *
     * @return The candidates.
     */
    default ImmutableSet<T> getCandidates()
    {
        return ImmutableSet.of();
    }

    /**
     * Indicates if the candidates of this handler are all there are.
     * If this handler knows that the candidates he provides via {@link #getCandidates()}
     * are all that exists, indexed interactables can then fail quickly and
     * do not need to iterate over their contents with the predicate.
     *
     * @return True when the candidates are all that exists, false when not.
     */
    default boolean isFullyKnown()
    {
        return false;
    }
}
