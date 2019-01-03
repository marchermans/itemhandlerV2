package net.minecraftforge.container.api;

public interface IContainerOperationResult<T> {

    /**
     * The primary result of the transactional operation.
     *
     * For an insertion this is the left over stack. Might be null or empty.
     * For an extraction this is the extracted stack. Might be null or empty.
     *
     * @return The primary result of the operation.
     */
    T getPrimary();

    /**
     * The secondary result of the transactional operation.
     *
     * For an insertion this is the stack that was previously in the inserted slot.
     * For an extraction this is the stack left in the slot.
     *
     * @return The secondary result of the operation.
     */
    T getSecondary();

    /**
     * Returns the status of transactional operation.
     *
     * @return The status.
     */
    Status getStatus();

    /**
     * Quick utility indicator to check if the operation was successful.
     *
     * @return True when the operation was successful, false whne not.
     */
    default boolean wasSuccessful()
    {
        return getStatus().isSuccess();
    }

    enum Status {


        /**
         * The container transaction succeeded.
         */
        SUCCESS,

        /**
         * When inserting, the target slot contains something that can not be merged with the inserting object.
         *
         * In general this means that the operation is generally possible with the slot / transaction, yet
         * it can not be performed right now, because the current state of the transaction is conflicting
         * with the the operation. (Stack and or Fluids are not mergeable.)
         *
         * This is also returned if a slotless insertion was performed and the object was partially inserted,
         * yet a different slot returned conflicted during processing.
         *
         * In that case the {@link IContainerOperationResult#getPrimary()} method will contain the remainder,
         * and this state indicates that a partial insertion was successful, yet without modification of slots
         * no more insertion into the container is possible.
         */
        CONFLICTING,

        /**
         * When inserting, the targeted slot of the container is full.
         * When extracting, the targeted slot of the container is empty.
         */
        FAILURE,

        /**
         * General invalid method call or eg:
         *
         * When inserting, the stack can not be inserted into the target slot.
         * When extracting, the target slot is readonly.
         */
        INVALID;

        public boolean isSuccess(){
            return this == SUCCESS;
        }

        public boolean isFailure(){
            return this == FAILURE;
        }

        public boolean isInvalid(){
            return this == INVALID;
        }

        public boolean isConflicting() { return this == CONFLICTING; }
    }
}
