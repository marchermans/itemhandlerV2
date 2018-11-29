package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class TransactionResult {
    private final ItemStack result;
    private final ItemStack other;
    private final Type type;

    public TransactionResult(ItemStack result, ItemStack other, Type type) {
        this.result = result;
        this.other = other;
        this.type = type;

    }

    /**
     * Gets the resulting {@link ItemStack} of this transaction.
     * <p/>
     * When inserting, this is the leftover stack.<br/>
     * When extracting, this is the stack that was extracted.
     */
    @Nonnull
    ItemStack getResult() {
        return result;
    }

    /**
     * Gets the secondary resulting {@link ItemStack} of this transaction.
     * <p/>
     * When inserting, this is the inserted stack.<br/>
     * When extracting, this is the stack that is left in the slot.
     */
    @Nonnull
    ItemStack getOther() {
        return other;
    }

    /**
     * the Transaction type
     */
    @Nonnull
    Type getType() {
        return type;
    }

    enum Type {


        /**
         * The inventory transaction succeeded.
         */
        SUCCESS,

        /**
         * When inserting, the inventory has no room the item.
         * When extracting, the inventory is empty.
         */
        FAILURE,

        /**
         * When inserting, the stack is not can not be inserted.
         * When extracting, the stack can not be extracted.
         */
        INVALID,

        /**
         * the transaction was cancelled by a third party
         */
        CANCELLED;

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
