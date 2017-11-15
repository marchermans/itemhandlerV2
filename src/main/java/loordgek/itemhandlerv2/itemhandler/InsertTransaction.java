package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class InsertTransaction {
    @Nonnull
    private final ItemStack insertedStack;
    @Nonnull
    private final ItemStack leftoverStack;

    public InsertTransaction(@Nonnull ItemStack insertedStack, @Nonnull ItemStack leftoverStack) {
        this.insertedStack = insertedStack;
        this.leftoverStack = leftoverStack;
    }

    @Nonnull
    public ItemStack getInsertedStack() {
        return insertedStack;
    }

    @Nonnull
    public ItemStack getLeftoverStack() {
        return leftoverStack;
    }
}
