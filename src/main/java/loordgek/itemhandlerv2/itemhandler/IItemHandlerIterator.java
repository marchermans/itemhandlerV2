package loordgek.itemhandlerv2.itemhandler;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Iterator;

public interface IItemHandlerIterator extends Iterator<ItemStack> {

    boolean hasNext();

    boolean hasPrevious();

    int currentIndex();

    int nextIndex();

    int previousIndex();

    @Nonnull
    ItemStack next();

    @Nonnull
    ItemStack previous();
}
