package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    private Future<OrderReceipt> future = null;
    OrderReceipt o_r;

    @Before
    public void setUp() throws Exception {
        future = new Future<>();
        OrderReceipt o_r = new OrderReceipt("Seller",6,"BookTitle",10,15,2,3);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    /*
    Checks waiting mechanism -> make the future object wait for 100 ms, meanwhile assign it's result, and check if it recieved the result when finished waiting
     */
    public void getWithTime() {
        OrderReceipt result;
        result = future.get(100, TimeUnit.MILLISECONDS);
        assertNull(result);

        future.resolve(o_r);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        result = future.get(100, TimeUnit.MILLISECONDS);
        assertEquals(o_r,result);

    }

    @Test
    /*
    before resolving the result -> future.isDone() == false
    after resolving the result -> future.isDone() == true
     */
    public void resolve() {
        assertFalse(future.isDone());
        future.resolve(o_r);
        assertTrue(future.isDone());
    }

    @Test
    /*
    check the isDone() method for an undone future object
     */
    public void isDoneFalse() {
        assertFalse(future.isDone());
    }

    @Test
    /*
    check the isDone() method for a done future object
     */
    public void isDoneTrue() {
        future.resolve(o_r);
        assertTrue(future.isDone());
    }

    @Test
    /*
    check the method get() ->
    creates a thread t1 that suppose to wait for a result
    let the main thread resolve the result and notify t1
    check that the result of the get() method is the appropriate result
     */
    public void getWithOutTime() {

        Thread t1 = new Thread(()-> {
            future.get();
            });

        t1.start();
        synchronized (future) {
            future.resolve(o_r);
            future.notifyAll();
        }
        assertEquals(o_r, future.get());
    }
}