package warehouse;

/*
 *
 * This class implements a warehouse on a Hash Table like structure, 
 * where each entry of the table stores a priority queue. 
 * Due to your limited space, you are unable to simply rehash to get more space. 
 * However, you can use your priority queue structure to delete less popular items 
 * and keep the space constant.
 * 
 * @author Ishaan Ivaturi
 */ 
public class Warehouse {
    private Sector[] sectors;
    
    // Initializes every sector to an empty sector
    public Warehouse() {
        sectors = new Sector[10];

        for (int i = 0; i < 10; i++) {
            sectors[i] = new Sector();
        }
    }
    
    /**
     * Provided method, code the parts to add their behavior
     * @param id The id of the item to add
     * @param name The name of the item to add
     * @param stock The stock of the item to add
     * @param day The day of the item to add
     * @param demand Initial demand of the item to add
     */
    public void addProduct(int id, String name, int stock, int day, int demand) {
        evictIfNeeded(id);
        addToEnd(id, name, stock, day, demand);
        fixHeap(id);
    }

    /**
     * Add a new product to the end of the correct sector
     * Requires proper use of the .add() method in the Sector class
     * @param id The id of the item to add
     * @param name The name of the item to add
     * @param stock The stock of the item to add
     * @param day The day of the item to add
     * @param demand Initial demand of the item to add
     */
    private void addToEnd(int id, String name, int stock, int day, int demand) {
        // IMPLEMENT THIS METHOD

        int ind = id % 10;
        Product newPro = new Product(id, name, stock, day, demand);
        sectors[ind].add(newPro);
    }

    /**
     * Fix the heap structure of the sector, assuming the item was already added
     * Requires proper use of the .swim() and .getSize() methods in the Sector class
     * @param id The id of the item which was added
     */
    private void fixHeap(int id) {
        // IMPLEMENT THIS METHOD
        int ind = id%10;
        if(sectors[ind].getSize() != 1){
            sectors[ind].swim(sectors[ind].getSize());
        }
    }

    /**
     * Delete the least popular item in the correct sector, only if its size is 5 while maintaining heap
     * Requires proper use of the .swap(), .deleteLast(), and .sink() methods in the Sector class
     * @param id The id of the item which is about to be added
     */
    private void evictIfNeeded(int id) {
       // IMPLEMENT THIS METHOD
       int ind = id %10; 
       if(sectors[ind].getSize() == 5){
           sectors[ind].swap(1, 5);
           sectors[ind].deleteLast();
           sectors[ind].sink(1);
       }
    }

    /**
     * Update the stock of some item by some amount
     * Requires proper use of the .getSize() and .get() methods in the Sector class
     * Requires proper use of the .updateStock() method in the Product class
     * @param id The id of the item to restock
     * @param amount The amount by which to update the stock
     */
    public void restockProduct(int id, int amount) {
        // IMPLEMENT THIS METHOD
        int ind = id %10;
        int size = sectors[ind].getSize();
        for(int i = size; i > 0; i--){
            if(sectors[ind].get(i).getId() == id){
                sectors[ind].get(i).setStock(sectors[ind].get(i).getStock() + amount);
                break;
            }
        }
    }
    
    /**
     * Delete some arbitrary product while maintaining the heap structure in O(logn)
     * Requires proper use of the .getSize(), .get(), .swap(), .deleteLast(), .sink() and/or .swim() methods
     * Requires proper use of the .getId() method from the Product class
     * @param id The id of the product to delete
     */
    public void deleteProduct(int id) {
        // IMPLEMENT THIS METHOD
        int ind = id % 10;
        int size = sectors[ind].getSize();
        for(int i = size; i > 0; i--){
            if(sectors[ind].get(i).getId() == id){
                sectors[ind].swap(i, size);
                sectors[ind].deleteLast();
                sectors[ind].sink(i);
            }
        }
    }
    
    /**
     * Simulate a purchase order for some product
     * Requires proper use of the getSize(), sink(), get() methods in the Sector class
     * Requires proper use of the getId(), getStock(), setLastPurchaseDay(), updateStock(), updateDemand() methods
     * @param id The id of the purchased product
     * @param day The current day
     * @param amount The amount purchased
     */
    public void purchaseProduct(int id, int day, int amount) {
        // IMPLEMENT THIS METHOD
        for (int i = 1; i <= sectors[id % 10].getSize(); i++){
            if(id == sectors[id%10].get(i).getId() && sectors[id % 10].get(i).getStock() >= amount){
                sectors[id % 10].get(i).setLastPurchaseDay(day);
                sectors[id % 10].get(i).setStock(sectors[id % 10].get(i).getStock()-amount);
                sectors[id % 10].get(i).updateDemand(amount);
                fixHeap(id);
                sectors[id % 10].sink(i);
                break;
            }
        }
    }
    
    /**
     * Construct a better scheme to add a product, where empty spaces are always filled
     * @param id The id of the item to add
     * @param name The name of the item to add
     * @param stock The stock of the item to add
     * @param day The day of the item to add
     * @param demand Initial demand of the item to add
     */
    public void betterAddProduct(int id, String name, int stock, int day, int demand) {
        // IMPLEMENT THIS METHOD
        if(sectors[id % 10].getSize() < 5){
            addProduct(id, name, stock, day, demand);
        }
        else{
            boolean done = false;
            for(int i = id %10; i <sectors.length; i++){
                if(sectors[i].getSize() < 5){
                    sectors[i].add(new Product(id, name, stock, day, demand));
                    fixHeap(i);
                    done = true;
                    break;
                }
            }

            if(done == false){
                for(int i = 0; i < id%10; i++){
                    if(sectors[i].getSize() < 5){
                        sectors[i].add(new Product(id, name, stock, day, demand));
                        fixHeap(i);
                        done = true;
                        break;
                    }
                }
            }

            if(done == false){
                addProduct(id, name, stock, day, demand);
            }
        }
    }

    /*
     * Returns the string representation of the warehouse
     */
    public String toString() {
        String warehouseString = "[\n";

        for (int i = 0; i < 10; i++) {
            warehouseString += "\t" + sectors[i].toString() + "\n";
        }
        
        return warehouseString + "]";
    }

    /*
     * Do not remove this method, it is used by Autolab
     */ 
    public Sector[] getSectors () {
        return sectors;
    }
}
