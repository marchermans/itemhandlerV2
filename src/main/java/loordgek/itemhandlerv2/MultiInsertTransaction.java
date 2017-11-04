package loordgek.itemhandlerv2;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;


public class MultiInsertTransaction {

    private final NonNullList<ItemStack> insertedStack = NonNullList.create();

    private final NonNullList<ItemStack> leftoverStack = NonNullList.create();

    public NonNullList<ItemStack> getInsertedStack() {
        return insertedStack;
    }

    public NonNullList<ItemStack> getLeftoverStack() {
        return leftoverStack;
    }

    public void addInsertTransaction(InsertTransaction insertTransaction){
        insertedStack.add(insertTransaction.getInsertedStack());
        leftoverStack.add(insertTransaction.getLeftoverStack());
    }


}
