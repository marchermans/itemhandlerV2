package loordgek.itemhandlerv2;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class ItemHandlerHelperV2 {

    public static InsertTransaction split(@Nonnull ItemStack itemStack, int size) {
        ItemStack insert = ItemHandlerHelper.copyStackWithSize(itemStack, size);

        ItemStack leftover = itemStack.copy();
        leftover.setCount(itemStack.getCount() - insert.getCount());
        return new InsertTransaction(insert, leftover.getCount() != 0 ? leftover : ItemStack.EMPTY);
    }
}
