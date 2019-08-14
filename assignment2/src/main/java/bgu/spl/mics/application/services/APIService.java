package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.OrderBookEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.BookTickPair;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link OrderBookEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{

	//data members
	private CopyOnWriteArrayList<BookTickPair> orderSchedule;
	private ConcurrentHashMap<String,Future<OrderReceipt>> FutureList;
	private Customer customer;
	private static int number = 1;
	private CountDownLatch counter = BookStoreRunner.counter;


	public APIService(Customer customer, CopyOnWriteArrayList<BookTickPair> orderSchedule) {
		super("APIService " + number);
		number++;
		this.orderSchedule = orderSchedule;
		this.customer = customer;
		this.FutureList = new ConcurrentHashMap<>();
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, message -> {
			for(int i = 0; i < this.orderSchedule.size(); i++){
				String currBook = this.orderSchedule.get(i).getBookTitle();
				Integer currTick = this.orderSchedule.get(i).getTick();
				if(currTick == message.getCurrTick()){
					OrderBookEvent e = new OrderBookEvent(currBook,customer,message.getCurrTick());
					Future<OrderReceipt> future = sendEvent(e);
					if (future != null){ // there was a micro service who can handle this type of event
						FutureList.put(currBook,future);
					}
				}
			}
			for (Map.Entry<String, Future<OrderReceipt>> entry : this.FutureList.entrySet()){
				Future<OrderReceipt> future = entry.getValue();
				OrderReceipt o = future.get();
				if (o != null) { // selling process completed successfully
					customer.getCustomerReceiptList().add(o);
					sendEvent(new DeliveryEvent(customer));
				}
			}
			FutureList.clear();
		});

		subscribeBroadcast(TerminateBroadcast.class, message -> {
			this.terminate();
		});
		counter.countDown();
	}
}
