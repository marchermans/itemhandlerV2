package loordgek.itemhandlerv2;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;


public class MultiInsertTransaction {

    private final NonNullList<ItemStack> insertedStacks = NonNullList.create();

    private final NonNullList<ItemStack> leftoverStacks = NonNullList.create();

    public NonNullList<ItemStack> getInsertedStacks() {
        return insertedStacks;
    }

    public NonNullList<ItemStack> getLeftoverStacks() {
        return leftoverStacks;
    }

    public void addInsertTransaction(InsertTransaction insertTransaction){
        insertedStacks.add(insertTransaction.getInsertedStack());
        leftoverStacks.add(insertTransaction.getLeftoverStack());
    }


}
