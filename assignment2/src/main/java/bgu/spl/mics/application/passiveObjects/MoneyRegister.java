package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.application.BookStoreRunner;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {


	// data members
	private static MoneyRegister ourInstance = new MoneyRegister();
	private CopyOnWriteArrayList<OrderReceipt> receiptList;
	private int totalEarnings;

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		return ourInstance;

	}

	private MoneyRegister() {
		this.receiptList = new CopyOnWriteArrayList<>();
		this.totalEarnings = 0;
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		this.receiptList.add(r);
		this.totalEarnings = totalEarnings + r.getPrice();
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return totalEarnings;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		int cAmount = c.getAvailableCreditAmount();
		c.setAvailableCreditAmount(cAmount-amount);
		}


	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		BookStoreRunner.Serialize(filename, receiptList);

	}

}

