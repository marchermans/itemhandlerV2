package loordgek.itemhandlerv2;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface IItemHandlerObservable {
    //DO NOT MODIFY THE LIST
    //todo J9 make this private
    List<IItemHandlerObserver> itemObserverList();

    default void addObserver(IItemHandlerObserver itemHandlerObserver){
        itemObserverList().add(itemHandlerObserver);
    }

    default void removeObserver(IItemHandlerObserver itemHandlerObserver){
        itemObserverList().remove(itemHandlerObserver);
    }

    default void onInserted(int slot, ItemStack stack){
        for (IItemHandlerObserver observer : itemObserverList()){
            observer.onInserted(this, slot, stack);
        }
    }

    default void onExtracted(int slot, ItemStack stack){
        for (IItemHandlerObserver observer : itemObserverList()){
            observer.onExtracted(this, slot, stack);
        }
    }
}
