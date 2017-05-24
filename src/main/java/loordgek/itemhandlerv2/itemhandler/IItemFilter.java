package loordgek.itemhandlerv2.itemhandler;


import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

@FunctionalInterface
public interface IItemFilter extends Predicate<ItemStack> {

    @Override
    boolean test(ItemStack stack);
}
