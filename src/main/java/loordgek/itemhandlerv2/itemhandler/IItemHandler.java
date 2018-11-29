package loordgek.itemhandlerv2.itemhandler;


import loordgek.itemhandlerv2.filter.IStackFilter;
import net.minecraft.item.ItemStack;

public interface IItemHandler extends Iterable<ItemStack>{

    int size();

    TransactionResult insert(int slot, ItemStack stack, boolean simulate);

    TransactionResult insert(ItemStack stack, boolean simulate);

    TransactionResult extract(int slot, IStackFilter filter, int amount, boolean simulate);

    TransactionResult extract(IStackFilter filter, int amount, boolean simulate);}
