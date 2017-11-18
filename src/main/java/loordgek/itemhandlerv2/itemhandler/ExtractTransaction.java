package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ExtractTransaction {
    @Nonnull
    private final ItemStack extractedStack;

    @Nonnull
    private final ItemStack StackLeftInSlot;

    public ExtractTransaction(@Nonnull ItemStack extractedStack, @Nonnull ItemStack stackLeftInSlot) {
        this.extractedStack = extractedStack;
        StackLeftInSlot = stackLeftInSlot;
    }

    @Nonnull
    public ItemStack getExtractedStack() {
        return extractedStack;
    }

    @Nonnull
    public ItemStack getStackLeftInSlot() {
        return StackLeftInSlot;
    }
}
