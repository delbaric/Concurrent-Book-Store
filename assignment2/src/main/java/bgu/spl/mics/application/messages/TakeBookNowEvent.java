package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class TakeBookNowEvent implements Event<Boolean> {

    private Customer customer;
    private String currBook;

    public TakeBookNowEvent(Customer customer, String currBook){
        this.customer = customer;
        this.currBook = currBook;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getCurrBook() {
        return currBook;
    }
}
