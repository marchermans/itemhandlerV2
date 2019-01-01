package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface ITransaction {

    /**
     * Gets the resulting {@link ItemStack} of this transaction.
     * <p/>
     * When inserting, this is the leftover stack.<br/>
     * When extracting, this is the stack that was extracted.
     */
    ItemStack getResult();

    /**
     * Gets the secondary resulting {@link ItemStack} of this transaction.
     * <p/>
     * When inserting, this is the inserted stack.<br/>
     * When extracting, this is the stack that is left in the slot.
     */
    ItemStack getOther();

    /**
     * Cancels this transaction and invalidates it and all the ones issued after it.<br/>
     * If another transaction's cancellation has invalidated this one, an {@link IllegalStateException} will be thrown.
     */
    ItemStack cancel();

    /**
     * Confirms this transaction and invalidates it.<br/>
     * If another transaction was issued prior to this one and has not been completed yet, an {@link IllegalStateException} will be
     * thrown.<br/>
     * If another transaction's cancellation has invalidated this one, an {@link IllegalStateException} will be thrown.
     */
    ItemStack confirm();

    /**
     * Checks the validity of this transaction.
     */
    boolean isValid();

    /**
     * the Transaction type
     */
    @Nonnull
    Type getType();

    enum Type {


        /**
         * The inventory transaction succeeded.
         */
        SUCCESS,

        /**
         * When inserting, the inventory is full.
         * When extracting, the inventory is empty.
         */
        FAILURE,

        /**
         * When inserting, the stack is not can not be inserted.
         * When extracting, the stack can not be extracted or the filter did not match.
         */
        INVALID,

        /**
         * the transaction was cancelled by a third party
         */
        CANCELLED,

        /**
         * something else :)
         */
        UNDEFINED;

        boolean isSuccess(){
            return this == SUCCESS;
        }

        boolean isFailure(){
            return this == FAILURE;
        }

        boolean isInvalid(){
            return this == INVALID;
        }

        boolean isCancelled(){
            return this == CANCELLED;
        }
    }
}
