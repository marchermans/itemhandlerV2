package net.minecraftforge.interactable.api;

/**
 * Default implementation of {@link IInteractableOperationResult}.
 * Provides some static utility methods to quickly create relevant result types.
 *
 * @param <T> The type of the operation.
 */
public class InteractableOperationResult<T> implements IInteractableOperationResult<T> {

    /**
     * Creates a new operation result with a success status.
     *
     * @param primary The primary operation result. Null or some default value if not available.
     * @param secondary The secondary operation result. Null or some default value if not available.
     * @param <T> The type of the operation.
     *
     * @return An instance of {@link InteractableOperationResult} indicating success with the given primary and secondary results.
     */
    public static <T> IInteractableOperationResult<T> success(final T primary, final T secondary)
    {
        return new InteractableOperationResult<>(primary, secondary, Status.SUCCESS);
    }

    /**
     * Creates a new operation result with a failed status.
     *
     * @param <T> The type of the operation.
     *
     * @return An instance of {@link InteractableOperationResult} indicating failure.
     */
    public static <T> IInteractableOperationResult<T> failed()
    {
        return new InteractableOperationResult<>(null, null, Status.FAILURE);
    }

    /**
     * Creates a new operation result with a invalid status.
     *
     * @param <T> The type of the operation.
     *
     * @return An instance of {@link InteractableOperationResult} indicating an invalid operation.
     */
    public static <T> IInteractableOperationResult<T> invalid()
    {
        return new InteractableOperationResult<>(null, null, Status.INVALID);
    }

    /**
     * Creates a new operation result with a conflicting status.
     *
     * @param <T> The type of the operation.
     *
     * @return An instance of {@link InteractableOperationResult} indicating a conflicting operation.
     */
    public static <T> IInteractableOperationResult<T> conflicting()
    {
        return new InteractableOperationResult<>(null, null, Status.CONFLICTING);
    }

    /**
     * Creates a new operation result with a conflicting status.
     *
     * @param <T> The type of the operation.
     *
     * @return An instance of {@link InteractableOperationResult} indicating a conflicting operation.
     */
    public static <T> IInteractableOperationResult<T> conflicting(final T primary)
    {
        return new InteractableOperationResult<>(primary, null, Status.CONFLICTING);
    }

    /**
     * The primary object. Might be null depending on the status in {@link #status}
     *
     * @see IInteractableOperationResult.Status
     */
    private final T primary;

    /**
     * The secondary object. Might be null depending on the status in {@link #status}
     *
     * @see IInteractableOperationResult.Status
     */
    private final T secondary;

    /**
     * The status of the operation.
     */
    private final Status status;

    /**
     * Creates a new operation result.
     *
     * @param primary The primary operation result. Null or some default value if not available.
     * @param secondary The secondary operation result. Null or some default value if not available.
     * @param status The status of the operation.
     */
    public InteractableOperationResult(T primary, T secondary, Status status) {
        this.primary = primary;
        this.secondary = secondary;
        this.status = status;
    }

    @Override
    public T getPrimary() {
        return primary;
    }

    @Override
    public T getSecondary() {
        return secondary;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
