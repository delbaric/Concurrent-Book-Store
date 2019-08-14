package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

public class OrderBookEvent implements Event<OrderReceipt> {

    // data members
    private String BookTitle;
    private Customer customer;
    private int OrderTick;

    // constructor
    public OrderBookEvent(String BookTitle, Customer customer, int OrderTick){
        this.BookTitle = BookTitle;
        this.customer = customer;
        this.OrderTick = OrderTick;
    }

    public String getBookTitle() {
        return BookTitle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getOrderTick() {
        return OrderTick;
    }
}
