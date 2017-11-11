package loordgek.itemhandlerv2;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Iterator;

public interface IItemHandlerIterator extends Iterator<ItemStack> {

    boolean hasNext();

    boolean hasPrevious();

    int currentindex();

    int nextindex();

    int previousindex();

    @Nonnull
    ItemStack next();

    @Nonnull
    ItemStack previous();
}
