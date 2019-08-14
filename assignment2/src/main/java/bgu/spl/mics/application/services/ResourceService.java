package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliverNowEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{

	private static int number = 1;
	private ResourcesHolder resourcesHolder;
	private CountDownLatch counter = BookStoreRunner.counter;

	public ResourceService() {
		super("resourcesService " + number);
		this.resourcesHolder = ResourcesHolder.getInstance();
		number++;

	}

	@Override
	protected void initialize() {
		subscribeEvent(AcquireVehicleEvent.class, message-> {
			complete(message,true); // always resolve with true so MsgBus future will be cleared
			Future<DeliveryVehicle> RH_future = resourcesHolder.acquireVehicle();
			DeliveryVehicle vehicle1 = RH_future.get(); // waiting for available vehicle
			Future<DeliveryVehicle> MB_future = sendEvent(new DeliverNowEvent(vehicle1,message.getCustomer())); // sending the acquired vehicle to deliver
			if (MB_future != null) { // there was an available logistics to handle the event
				DeliveryVehicle vehicle2 = MB_future.get();
				if (vehicle2!= null) { // the deliver finished successfully
					resourcesHolder.releaseVehicle(vehicle2);
				}
				else { // the deliver didn't happen so we release the taken vehicle
					resourcesHolder.releaseVehicle(vehicle1);
				}
			}
			else {
				resourcesHolder.releaseVehicle(vehicle1);
			}


		});

		subscribeBroadcast(TerminateBroadcast.class, message -> {
			this.terminate();
		});
		counter.countDown();
	}

}
