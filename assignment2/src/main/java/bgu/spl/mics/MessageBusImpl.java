package bgu.spl.mics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {


	// START of Singleton constructor

	// Data members
	private static MessageBusImpl MessageBus = new MessageBusImpl();
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> MessageQueueMap;
	private ConcurrentHashMap<Class, LinkedBlockingQueue<MicroService>> EventMap;
	private ConcurrentHashMap<Class, CopyOnWriteArrayList<MicroService>> BroadcastMap;
	private ConcurrentHashMap<Message, Future> MessageFutureMap;
	private Object BroadcastLocker;
	private Object EventLocker;




	public static MessageBusImpl getInstance() {
		return MessageBus;
	}

	private MessageBusImpl() {
		MessageQueueMap = new ConcurrentHashMap<>();
		EventMap = new ConcurrentHashMap<>();
		BroadcastMap = new ConcurrentHashMap<>();
		MessageFutureMap = new ConcurrentHashMap<>();
		BroadcastLocker = new Object();
		EventLocker = new Object();
	}

	// END of Singleton constructor


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!EventMap.containsKey(type)){ // this is the first M.S that subscribes to this "type" of event
			LinkedBlockingQueue<MicroService> q = new LinkedBlockingQueue<>(); // new M.S blocking Q (round-robin)
			try {
				q.put(m);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			EventMap.put(type,q); // adding the new "type" and it's Q to the Event Map
		} else { // this type is already on the Map, just need to add m M.S to the matching blocking Q
			try {
				this.EventMap.get(type).put(m);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (this) {
			if (!BroadcastMap.containsKey(type)) { // this is the first M.S that subscribes to this "type" of broadcast
				CopyOnWriteArrayList<MicroService> lst = new CopyOnWriteArrayList<>();
				lst.add(m);
				BroadcastMap.put(type, lst);

			}
			else { // this type is already on the Map, just need to add m M.S to the matching list
				this.BroadcastMap.get(type).add(m);
			}
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		this.MessageFutureMap.get(e).resolve(result);
		this.MessageFutureMap.remove(e); // delete the specific (Message,Future) from the list
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (BroadcastLocker) {
			CopyOnWriteArrayList<MicroService> lst = BroadcastMap.get(b.getClass());
			if (lst != null) { // There is at least 1 M.S who subscribed to this broadcast
				for (int i = 0; i < lst.size(); i++) {
					try {
						this.MessageQueueMap.get(lst.get(i)).put(b); // add broadcast b to every Q of each M.S who subscribed to this broadcast
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (EventLocker) {
			Future<T> f = null;
			MicroService m = null;
			LinkedBlockingQueue<MicroService> q = this.EventMap.get(e.getClass());
			if (q != null) { // there is at least 1 suitable MicroService to handle event e
				try {
					m = q.take(); // m is the first M.S in the Q
					q.put(m); // putting M.S m in the back of the Q (round-robin)
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				f = new Future<>(); // create a new future instance

				this.MessageFutureMap.put(e, f); // add e and future to map
				try {
					this.MessageQueueMap.get(m).put(e); // add event e to the Q of M.S m
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			return f;
		}
	}

	@Override
	public void register(MicroService m) {
		LinkedBlockingQueue<Message> q = new LinkedBlockingQueue<>(); // create a new blocking Q for M.S m
		this.MessageQueueMap.put(m,q);

	}

	@Override
	public void unregister(MicroService m) {

		if(this.MessageQueueMap.containsKey(m)){

			LinkedBlockingQueue q1 = this.MessageQueueMap.get(m);
			synchronized (EventLocker) {
				this.MessageQueueMap.remove(m); // removes the Queue of m
				for (Map.Entry<Class, LinkedBlockingQueue<MicroService>> entry : this.EventMap.entrySet()) { //iterates on all the Event types, and removes the micro service m from it's queue if present
					LinkedBlockingQueue q = entry.getValue();
					q.remove(m);
					if (q.isEmpty()) {
						EventMap.remove(entry.getKey());
					}
				}
			}
			int size = q1.size();
			for (int i = 0; i < size; i++){
				Event e = (Event)q1.poll();
				complete(e,null);
			}

			synchronized (BroadcastLocker) {
				for (Map.Entry<Class, CopyOnWriteArrayList<MicroService>> entry : this.BroadcastMap.entrySet()) {  //iterates on all the Broadcast types, and removes the micro service m from it's list if present
					CopyOnWriteArrayList lst = entry.getValue();
					lst.remove(m);
					if (lst.isEmpty()) {
						BroadcastMap.remove(entry.getKey());
					}
				}
			}
		}

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message result;
		if (!MessageQueueMap.containsKey(m)){
			throw new IllegalStateException();
		}
		result = this.MessageQueueMap.get(m).take(); // attempts to take the first msg in the Q
		return result;
	}
}