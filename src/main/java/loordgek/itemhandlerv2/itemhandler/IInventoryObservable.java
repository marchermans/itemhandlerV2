package loordgek.itemhandlerv2.itemhandler;

public interface IInventoryObservable {

    void addObserver(IInventoryObserver observer);

    void removeObserver(IInventoryObserver observer);
}
