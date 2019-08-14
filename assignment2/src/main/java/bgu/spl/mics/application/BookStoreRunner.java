package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static CountDownLatch counter;

    public static void main(String[] args) {
            HashMap<Integer, Customer> FirstOutputFile = new HashMap<>();
            try {
                byte[] inputByBytes = Files.readAllBytes(Paths.get(args[0]));
                String inputByString = new String(inputByBytes);
                Gson gson = new Gson();
                Input input = new Gson().fromJson(inputByString, Input.class);


                // start all micro services
                Thread[] ThreadArray = createBookStore(input, FirstOutputFile);
                for (int i = 0; i < ThreadArray.length; i++) {
                    ThreadArray[i].start();
                }

                // main thread waits for all micro services to finish
                for (int i = 0; i < ThreadArray.length; i++) {
                    try {
                        ThreadArray[i].join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // function that creates the output files
            CreateOutputFiles(FirstOutputFile, args[1], args[2], args[3], args[4]);

    }

    private static Thread[] createBookStore(Input input, HashMap<Integer,Customer> FirstOutputFile){

        Inventory inventory = Inventory.getInstance();
        inventory.load(input.initialInventory);

        // Create ResourcesHolder
        ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
        resourcesHolder.load(input.initialResources[0].vehicles);

        //Create Services
        int size = input.services.inventoryService + input.services.logistics + input.services.resourcesService + input.services.selling + input.services.customers.length + 1;
        int currPlace = 0;
        Thread[] ThreadArray = new Thread[size];
        counter = new CountDownLatch(size -1);


        // SellingService
        int sellingAmount = input.services.selling;
        for (int i = 0; i < sellingAmount ; i++){
            SellingService s = new SellingService();
            Thread t = new Thread(s);
            ThreadArray[currPlace] = t;
            currPlace++;
        }

        //InventoryService
        int inventoryAmount = input.services.inventoryService;
        for (int i = 0; i < inventoryAmount ; i++){
            InventoryService s = new InventoryService();
            Thread t = new Thread(s);
            ThreadArray[currPlace] = t;
            currPlace++;
        }

        // LogisticsService
        int logisticsAmount = input.services.logistics;

        for (int i = 0; i < logisticsAmount ; i++){
            LogisticsService s = new LogisticsService();
            Thread t = new Thread(s);
            ThreadArray[currPlace] = t;
            currPlace++;
        }

        // ResourceService
        int resourcesAmount = input.services.resourcesService;
        for (int i = 0; i < resourcesAmount ; i++){
            ResourceService s = new ResourceService();
            Thread t = new Thread(s);
            ThreadArray[currPlace] = t;
            currPlace++;
        }

        // APIService
        Input.Services.customersFromJson[] customers = input.services.customers;

        for (int i = 0; i < customers.length; i ++){
            Customer customer = new Customer(customers[i].id, customers[i].name, customers[i].address, customers[i].distance, customers[i].creditCard.number, customers[i].creditCard.amount);
            FirstOutputFile.put(customer.getId(),customer); // add the customer with it's id to the hash map
            CopyOnWriteArrayList<BookTickPair> orderSchedule = new CopyOnWriteArrayList<>();
            for (int j = 0; j < customers[i].orderSchedule.length; j ++) {
                orderSchedule.add(customers[i].orderSchedule[j]);
            }
            APIService s = new APIService(customer,orderSchedule);
            Thread t = new Thread(s);
            ThreadArray[currPlace] = t;
            currPlace++;
        }

        // TimeService
        TimeService time =  new TimeService(input.services.time.speed, input.services.time.duration);
        Thread t = new Thread(time);
        ThreadArray[currPlace] = t;

        return ThreadArray;
    }

    public class Input {

        private BookInventoryInfo[] initialInventory;

        private Input.initialResourcesFromJson[] initialResources;

        private class initialResourcesFromJson {
            private DeliveryVehicle[] vehicles;
        }

        private Input.Services services;
        private class Services {
            Input.Services.timeService time;
            private class timeService {int speed;int duration;}
            int selling;
            int inventoryService;
            int logistics;
            int resourcesService;
            Input.Services.customersFromJson[] customers;
            private class customersFromJson {
                int id;
                String name;
                String address;
                int distance;
                Input.Services.customersFromJson.creditCard creditCard;
                private class creditCard {int number;int amount;}
                BookTickPair[] orderSchedule;
            }
        }
    }
    private static void CreateOutputFiles(HashMap<Integer,Customer> FirstOutputFile, String o1, String o2, String o3, String o4){

        Serialize(o1,FirstOutputFile); // first output file

        Inventory i = Inventory.getInstance();
        i.printInventoryToFile(o2); // second output file

        MoneyRegister m = MoneyRegister.getInstance();
        m.printOrderReceipts(o3); //third output file

        Serialize(o4,m); // fourth output file


    }
    // method that recieves a filename and a serializable object, and codes it to a file
    public static void Serialize (String filename, Serializable s){
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(filename);
            out = new ObjectOutputStream(fos);
            out.writeObject(s);

            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // method that recieves a filename and a result object, and uncodes the file to an actual object
    public static <T> T Deserialize (String filename, T result){
        try
        {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            result = (T)in.readObject();

            in.close();
            file.close();
            return result;
        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }

        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }
        return null;
    }
}





