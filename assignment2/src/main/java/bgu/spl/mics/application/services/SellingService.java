package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.concurrent.CountDownLatch;


/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link OrderBookEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{

	private static int number = 1;
	private MoneyRegister moneyRegister;
	private int CurrTick;
	private CountDownLatch counter = BookStoreRunner.counter;


	public SellingService() {
		super("selling " + number);
		moneyRegister = MoneyRegister.getInstance();
		number++;


	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, message -> {
			this.CurrTick = message.getCurrTick();
		});

		subscribeEvent(OrderBookEvent.class, message -> {
			int processTick = this.CurrTick;
			Future<Integer> future = sendEvent(new CheckAvailability(message.getBookTitle(),message.getCustomer())); // sending a CheckAvailability event with the book title and the corresponding customer's credit amount
			boolean possible = false;
			if (future != null) { // there is inventory service available
				Integer result = future.get();
				Customer customer = message.getCustomer();
				if (result != null && result != -1) { //there is a inventory service that handled the CA event, and the selling is possible
					synchronized (customer) { // make sure the customer does not purchase two books concurrently
						if (customer.getAvailableCreditAmount() >= result) { // make sure that the customer still have enough money
							Future<Boolean> future1 = sendEvent(new TakeBookNowEvent(customer, message.getBookTitle())); // send event to actually purchase the book
							if (future1 != null){ // there is inventory service available
								Boolean completeSell = future1.get();
								if (completeSell != null && completeSell) { // the book was taken out of the inventory
									OrderReceipt CurrReceipt = new OrderReceipt(this.getName(), customer.getId(), message.getBookTitle(), result, processTick, message.getOrderTick(), processTick);
									moneyRegister.file(CurrReceipt);
									moneyRegister.chargeCreditCard(customer,result);
									complete(message, CurrReceipt); // updates the "future" in APIService that the selling process succeeded
									possible = true;
								}
							}
						}
					}
				}
			}
			if (!possible){
				complete(message,null);
			}

		});
		subscribeBroadcast(TerminateBroadcast.class, message -> {
			this.terminate();
		});
		counter.countDown();

	}

}
