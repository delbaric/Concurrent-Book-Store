package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class DeliverNowEvent implements Event<DeliveryVehicle> {

    DeliveryVehicle vehicle;
    Customer customer;

    public DeliverNowEvent(DeliveryVehicle vehicle, Customer customer){
        this.vehicle = vehicle;
        this.customer = customer;
    }
    public DeliveryVehicle getVehicle(){
        return vehicle;
    }
    public Customer getCustomer(){
        return customer;
    }
}
