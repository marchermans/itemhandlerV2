package net.minecraftforge.container.api;

/**
 * Default implementation of {@link IContainerOperationResult}.
 * Provides some static utility methods to quickly create relevant result types.
 *
 * @param <T> The type of the operation.
 */
public class ContainerOperationResult<T> implements IContainerOperationResult<T> {

    /**
     * Creates a new operation result with a success status.
     *
     * @param primary The primary operation result. Null or some default value if not available.
     * @param secondary The secondary operation result. Null or some default value if not available.
     * @param <T> The type of the operation.
     *
     * @return An instance of {@link ContainerOperationResult} indicating success with the given primary and secondary results.
     */
    public static <T> IContainerOperationResult<T> success(final T primary, final T secondary)
    {
        return new ContainerOperationResult<>(primary, secondary, Status.SUCCESS);
    }

    /**
     * Creates a new operation result with a failed status.
     *
     * @param <T> The type of the operation.
     *
     * @return An instance of {@link ContainerOperationResult} indicating failure.
     */
    public static <T> IContainerOperationResult<T> failed()
    {
        return new ContainerOperationResult<>(null, null, Status.FAILURE);
    }

    /**
     * Creates a new operation result with a invalid status.
     *
     * @param <T> The type of the operation.
     *
     * @return An instance of {@link ContainerOperationResult} indicating an invalid operation.
     */
    public static <T> IContainerOperationResult<T> invalid()
    {
        return new ContainerOperationResult<>(null, null, Status.INVALID);
    }

    /**
     * Creates a new operation result with a conflicting status.
     *
     * @param <T> The type of the operation.
     *
     * @return An instance of {@link ContainerOperationResult} indicating a conflicting operation.
     */
    public static <T> IContainerOperationResult<T> conflicting()
    {
        return new ContainerOperationResult<>(null, null, Status.CONFLICTING);
    }

    /**
     * The primary object. Might be null depending on the status in {@link #status}
     *
     * @see IContainerOperationResult.Status
     */
    private final T primary;

    /**
     * The secondary object. Might be null depending on the status in {@link #status}
     *
     * @see IContainerOperationResult.Status
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
    public ContainerOperationResult(T primary, T secondary, Status status) {
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
