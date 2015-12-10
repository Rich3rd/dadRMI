package cart;

import java.util.*;
import java.rmi.*;
import database.BookDetails;
import Interface.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ShoppingCart {

    private HashMap items = null;

    public ShoppingCart() {
        items = new HashMap();
    }

    public synchronized void add(String bookId, BookDetails book) {
        if (items.containsKey(bookId)) {
            ShoppingCartItem scitem = (ShoppingCartItem) items.get(bookId);
            scitem.incrementQuantity();
        } else {
            ShoppingCartItem newItem = new ShoppingCartItem(book);
            items.put(bookId, newItem);
        }
    }

    public synchronized void remove(String bookId) {
        if (items.containsKey(bookId)) {
            ShoppingCartItem scitem = (ShoppingCartItem) items.get(bookId);
            scitem.decrementQuantity();
            if (scitem.getQuantity() <= 0) {
                items.remove(bookId);
            }
        }
    }

    public synchronized List getItems() {
        List results = new ArrayList();
        Iterator i = items.values().iterator();
        while (i.hasNext()) {
            results.add(i.next());
        }
        return (results);
    }

    protected void finalize() throws Throwable {
        items.clear();
    }

    public synchronized int getNumberOfItems() {
        int numberOfItems = 0;
        for (Iterator i = getItems().iterator(); i.hasNext();) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            numberOfItems += item.getQuantity();
        }
        return numberOfItems;
    }

    public synchronized double getTotal() {
        double amount = 0.0;
        for (Iterator i = getItems().iterator(); i.hasNext();) {
            ShoppingCartItem item = (ShoppingCartItem) i.next();
            BookDetails bookDetails = (BookDetails) item.getItem();
            amount += (item.getQuantity() * bookDetails.getPrice());
        }
        return roundOff(amount);
    }
    
    public synchronized double convertUSD(double price) throws RemoteException, NotBoundException
    {
        Registry registry = LocateRegistry.getRegistry("172.20.10.2", Constant.RMI_PORT);
        TestRemote remote = (TestRemote) registry.lookup(Constant.RMI_ID);
        return roundOff(remote.toUSD(price));
    }
    
    public synchronized double convertUK(double price) throws RemoteException, NotBoundException
    {
        Registry registry = LocateRegistry.getRegistry("172.20.10.2", Constant.RMI_PORT);
        TestRemote remote = (TestRemote) registry.lookup(Constant.RMI_ID);
        return roundOff(remote.toUK(price));
    }
    
    public synchronized double convertMYR(double price) throws RemoteException, NotBoundException
    {
        Registry registry = LocateRegistry.getRegistry("172.20.10.2", Constant.RMI_PORT);
        TestRemote remote = (TestRemote) registry.lookup(Constant.RMI_ID);
        return roundOff(remote.toMYR(price));
    }

    static private double roundOff(double x) {
        return Math.round(x * 100) / 100.0;
    }

    public synchronized void clear() {
        items.clear();
    }
}
