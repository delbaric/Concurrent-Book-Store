package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.CheckAvailability;
import bgu.spl.mics.application.messages.TakeBookNowEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{

	private static int number = 1;
	private Inventory inventory;
	private CountDownLatch counter = BookStoreRunner.counter;

	public InventoryService() {
		super("inventoryService " + number);
		inventory = Inventory.getInstance();
		number++;

	}

	@Override
	protected void initialize() {
		subscribeEvent(CheckAvailability.class, message -> {
			String currBook = message.getBookTitle();
			Integer price = inventory.checkAvailabiltyAndGetPrice(currBook);
			if (price == -1 | message.getCustomer().getAvailableCreditAmount() < price){ //if the book is not in stock or the customer does not have enough credit
				complete(message,-1);
			}
			else{ //customer has enough credit and the book is in stock
				complete(message,price);
			}
		});

		subscribeEvent(TakeBookNowEvent.class, message -> {
			boolean possible = false;
			OrderResult result = inventory.take(message.getCurrBook());
			if (result.equals(OrderResult.SUCCESSFULLY_TAKEN)) {
				possible = true;
			}
			complete(message,possible);
		});

		subscribeBroadcast(TerminateBroadcast.class, message -> {

			this.terminate();
		});
		counter.countDown();

	}

}
