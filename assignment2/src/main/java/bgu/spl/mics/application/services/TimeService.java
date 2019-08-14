package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.BookStoreRunner;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	//data members
	private int speed;
	private int duration;
	private int currTick;
	private Object locker;
	private CountDownLatch counter;


	public TimeService(int speed, int duration) {
		super("time");
		this.speed = speed;
		this.duration = duration;
		this.currTick = 1;
		this.locker = new Object();
		counter = BookStoreRunner.counter;
	}

	@Override
	protected void initialize() {
		try {
			counter.await(); // make sure the service starts only after all services have been initialized
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		subscribeBroadcast(TerminateBroadcast.class, message -> {
			this.terminate();
		});

		TimerTask repeatedTask = new TimerTask() {
			public void run() {
				if (currTick <= duration) {
					sendBroadcast(new TickBroadcast(currTick));
					currTick++;
				}
				else {
					synchronized (locker) {
						sendBroadcast(new TerminateBroadcast());
						locker.notifyAll(); // release the Timer Service Thread so it will finish and terminate
						cancel();
					}
				}
			}
		};

		// using schedule task we're running the clock ticks
		Timer timer = new Timer("Timer");
		timer.scheduleAtFixedRate(repeatedTask,0, speed);
		synchronized (locker) {
			try {
				locker.wait(); // wait for the Timer Task Thread to send all it's ticks
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			timer.cancel();
		}
	}
}

