package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a OrderBookEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {

	// data members
	private static int orderId = 1;
	private String seller;
	private int customer;
	private String bookTitle;
	private int price;
	private int issuedTick;
	private int orderTick;
	private int proccessTick;

	public OrderReceipt (String seller, int customer, String bookTitle, int price, int issuedTick, int orderTick, int proccessTick) {
		this.seller = seller;
		this.customer = customer;
		this.bookTitle = bookTitle;
		this.price = price;
		this.issuedTick = issuedTick;
		this.orderTick = orderTick;
		this.proccessTick = proccessTick;
		orderId++;
	}
	
	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {
		return this.orderId;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return this.seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return customer;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return bookTitle;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {
		return issuedTick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase request.
     */
	public int getOrderTick() {
		return orderTick;
	}
	
	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {
		return proccessTick;
	}

	public String toString(){
		return "{order id = " + this.orderId + ", seller name = " + this.seller + ", customer id = " + this.customer + ", book title = " + this.bookTitle + ", price = " + this.price + ", issued tick = " + this.issuedTick + ", order tick = " + this.orderTick + ", process tick = " + this.proccessTick + "}";
	}
}
