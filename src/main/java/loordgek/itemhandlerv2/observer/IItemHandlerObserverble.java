package loordgek.itemhandlerv2.observer;

public interface IItemHandlerObserverble {

    /*
    o().addObserver((handler, slot, oldStack, newStack, flow) -> {
        if (flow.onInsert())
            System.out.println("hello");
    });
    */
    void addObserver(IItemHandlerObserver observer);
}
