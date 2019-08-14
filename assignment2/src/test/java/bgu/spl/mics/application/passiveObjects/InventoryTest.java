//package bgu.spl.mics.application.passiveObjects;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//import static org.junit.Assert.*;
//
//public class InventoryTest {
//
//    private Inventory inventory = null;
//
//
//    @Before
//    public void setUp() throws Exception {
//        inventory = Inventory.getInstance();
//    }
//
//    @After
//    public void tearDown() throws Exception {
////    inventory.setMap(new ConcurrentHashMap<String, BookInventoryInfo>());
////    inventory.setMapForFile(new ConcurrentHashMap<String, Integer>());
//    }
//
//    @Test
//    /*
//    There is only 1 instance of inventory (singleton)
//     */
//    public void getInstance() {
//        Inventory inventory2 = Inventory.getInstance();
//        assertEquals(inventory,inventory2);
//    }
//
//    @Test
//    /*
//    This is empty input load test.
//     We are making sure out map hasn't changed it's size due to the load, and that the process has passed successfully.
//     */
//    public void loadEmptyInput() {
//        BookInventoryInfo[] bookArray = new BookInventoryInfo[0];
//        inventory.load(bookArray);
//        System.out.println(inventory.getMap().toString());
//        assertEquals(0,inventory.getMap().size());
//    }
//
//    @Test
//    /*
//    This is load functionality test.
//    We are making sure out map size has increased according to input array size.
//    We are making sure each book in input array has been added to our map.
//     */
//    public void load() {
//        BookInventoryInfo b1 = new BookInventoryInfo("Harry Potter",10,55);
//        BookInventoryInfo b2 = new BookInventoryInfo("Charlie in the chocolate factory",5,70);
//        BookInventoryInfo b3 = new BookInventoryInfo("Israel",2,160);
//
//        BookInventoryInfo[] bookArray = new BookInventoryInfo[3];
//        bookArray[0] = b1;
//        bookArray[1] = b2;
//        bookArray[2] = b3;
//
//        inventory.load(bookArray);
//        assertEquals(3,inventory.getMap().size());
//        assertEquals("Harry Potter", inventory.getMap().get("Harry Potter").getBookTitle());
//        assertEquals(5,inventory.getMap().get("Charlie in the chocolate factory").getAmountInInventory());
//        assertEquals(160, inventory.getMap().get("Israel").getPrice());
//    }
//
//    @Test
//    /*
//    Trying adding a null book should end with no change to our map, and should return OrderResult.NOT_IN_STOCK
//     */
//    public void takeNOT_IN_STOCK_NULL() {
//
//        BookInventoryInfo b1 = new BookInventoryInfo("Harry Potter",10,55);
//        BookInventoryInfo b2 = new BookInventoryInfo("Charlie in the chocolate factory",5,70);
//        BookInventoryInfo b3 = new BookInventoryInfo("Israel",2,160);
//
//        // independently insert books to the map, in order to check the take() method without depending on the load() method
//        inventory.getMap().put(b1.getBookTitle(),b1);
//        inventory.getMap().put(b2.getBookTitle(),b2);
//        inventory.getMap().put(b3.getBookTitle(),b3);
//
//        assertEquals(OrderResult.NOT_IN_STOCK, inventory.take("BOOK_THAT_DOES_NOT_EXIST_IN_THE_INVENTORY"));
//
//    }
//
//    @Test
//     /*
//    Trying adding an amount=0 book should end with no change to our map, and should return OrderResult.NOT_IN_STOCK
//     */
//    public void takeNOT_IN_STOCK_0() {
//        BookInventoryInfo b1 = new BookInventoryInfo("Harry Potter",10,55);
//        BookInventoryInfo b2 = new BookInventoryInfo("Charlie in the chocolate factory",5,70);
//        BookInventoryInfo b3 = new BookInventoryInfo("Israel",0,160);
//
//        // independently insert books to the map, in order to check the take() method without depending on the load() method
//        inventory.getMap().put(b1.getBookTitle(),b1);
//        inventory.getMap().put(b2.getBookTitle(),b2);
//        inventory.getMap().put(b3.getBookTitle(),b3);
//
//        assertEquals(OrderResult.NOT_IN_STOCK, inventory.take("Israel"));
//
//
//    }
//
//
//
//    @Test
//    /*
//    Trying to take a book that does exist in the inventory. the method should return SUCCESSFULLY_TAKEN and reduce the amount of the book by one
//     */
//    public void takeSUCCESSFULLY_TAKEN() {
//        BookInventoryInfo b1 = new BookInventoryInfo("Harry Potter",10,55);
//        BookInventoryInfo b2 = new BookInventoryInfo("Charlie in the chocolate factory",5,70);
//        BookInventoryInfo b3 = new BookInventoryInfo("Israel",5,160);
//
//        // independently insert books to the map, in order to check the take() method without depending on the load() method
//        inventory.getMap().put(b1.getBookTitle(),b1);
//        inventory.getMap().put(b2.getBookTitle(),b2);
//        inventory.getMap().put(b3.getBookTitle(),b3);
//
//        inventory.getMapForFile().put(b1.getBookTitle(),b1.getAmountInInventory());
//
//        inventory.take("Harry Potter");
//        Integer x = 9;
//        // checking that both map and mapForFile are updated
//        assertEquals(9, inventory.getMap().get("Harry Potter").getAmountInInventory());
//        assertEquals(x,inventory.getMapForFile().get("Harry Potter"));
//        assertEquals(OrderResult.SUCCESSFULLY_TAKEN, inventory.take("Harry Potter"));
//    }
//
//    @Test
//    /*
//    If the book is in stock, should return it's price
//     */
//    public void checkAvailabiltyAndGetPriceAvailable() {
//        BookInventoryInfo b1 = new BookInventoryInfo("Harry Potter", 10, 55);
//
//
//        // independently insert books to the map, in order to check the take() method without depending on the load() method
//        inventory.getMap().put(b1.getBookTitle(), b1);
//
//
//        assertEquals(55, inventory.checkAvailabiltyAndGetPrice("Harry Potter"));
//
//    }
//    @Test
//    /*
//    If the book is not in stock or it's amount is 0, should return -1
//     */
//    public void checkAvailabiltyAndGetPriceNotAvailable() {
//
//        BookInventoryInfo b1 = new BookInventoryInfo("Harry Potter", 0, 55);
//
//
//        // independently insert books to the map, in order to check the take() method without depending on the load() method
//        inventory.getMap().put(b1.getBookTitle(), b1);
//
//
//        assertEquals(-1, inventory.checkAvailabiltyAndGetPrice("BOOK_THAT_DOES_NOT_EXIST_IN_THE_INVENTORY"));
//        assertEquals(-1, inventory.checkAvailabiltyAndGetPrice("Harry Potter"));
//        }
//
//
//}