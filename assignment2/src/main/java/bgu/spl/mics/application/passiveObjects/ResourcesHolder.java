package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	private static ResourcesHolder ourInstance = new ResourcesHolder();
	private BlockingQueue<DeliveryVehicle> vehicleQueue;
	private BlockingQueue<Future<DeliveryVehicle>> FutureQueue;


	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return ourInstance;
	}

	private ResourcesHolder(){
		this.vehicleQueue = new LinkedBlockingQueue<>();
		this.FutureQueue = new LinkedBlockingQueue<>();
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		synchronized (this) {
			Future<DeliveryVehicle> f = new Future<>();
			if (!vehicleQueue.isEmpty()) { // if there is an available vehicle -> resolve future now and return it
				f.resolve(vehicleQueue.poll());
			} else { // there is no future now, will be resolved at some point
				this.FutureQueue.add(f);
			}
			return f;
		}
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		synchronized (this) {
			if (FutureQueue.isEmpty()) {
				vehicleQueue.add(vehicle);
			} else {
				FutureQueue.poll().resolve(vehicle);
			}
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for (int i = 0; i < vehicles.length; i++ ){
			this.vehicleQueue.add(vehicles[i]);
		}
	}

}
