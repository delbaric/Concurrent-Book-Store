package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.BookStoreRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */

public class Inventory {

	// data members
	private static Inventory ourInstance = new Inventory();
	private ConcurrentHashMap<String,BookInventoryInfo> map;
	private HashMap<String,Integer> mapForFile;


	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		return ourInstance;
	}
	private Inventory() {
		this.map = new ConcurrentHashMap<>();
		this.mapForFile = new HashMap<>();
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		for (int i=0; i<inventory.length; i++){
			map.put(inventory[i].getBookTitle(), inventory[i]);
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		BookInventoryInfo b = map.get(book);
		synchronized (b) {
			if (b == null || b.getAmountInInventory() == 0) {
				return OrderResult.NOT_IN_STOCK;
			} else { // there is at least 1 book copy available
				b.reduceAmountInInventory(); // reduce by 1 the amount of the book in inventory
				return OrderResult.SUCCESSFULLY_TAKEN;
			}
		}
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		BookInventoryInfo b = map.get(book);
		if (b == null || b.getAmountInInventory() == 0) {
			return -1;
		}
		else {
			return b.getPrice();
		}
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename) { //
		for (Map.Entry<String, BookInventoryInfo> entry : map.entrySet()) {
			mapForFile.put(entry.getKey(), entry.getValue().getAmountInInventory());
		}
		BookStoreRunner.Serialize(filename, mapForFile);
	}
}
