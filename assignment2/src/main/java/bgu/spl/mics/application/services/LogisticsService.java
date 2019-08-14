package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliverNowEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;

import java.util.concurrent.CountDownLatch;


/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {

	private static int number=1;
	private CountDownLatch counter = BookStoreRunner.counter;


	public LogisticsService() {
		super("logistics " + number);
		number++;
	}

	@Override
	protected void initialize() {
		subscribeEvent(DeliveryEvent.class, message -> {
			sendEvent(new AcquireVehicleEvent(message.getCustomer())); // sends a deliveryEvent to resource service, and does NOT wait for an answer

		});
		subscribeEvent(DeliverNowEvent.class, message -> {
			Customer customer = message.getCustomer();
			message.getVehicle().deliver(customer.getAddress(),customer.getDistance());
			complete(message,message.getVehicle()); // actually makes the delivery and sends back the vehicle in order to release it


		});
		subscribeBroadcast(TerminateBroadcast.class, message -> {
			this.terminate();
		});
		counter.countDown();
	}

}
