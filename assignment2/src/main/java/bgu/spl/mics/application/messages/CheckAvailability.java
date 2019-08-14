package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class CheckAvailability implements Event<Integer> {

    private String BookTitle;
    private Customer customer;

    public CheckAvailability(String BookTitle,Customer customer){
        this.BookTitle = BookTitle;
        this.customer = customer;

    }

    public String getBookTitle() {
        return BookTitle;
    }

    public Customer getCustomer() {
        return customer;
    }

}
