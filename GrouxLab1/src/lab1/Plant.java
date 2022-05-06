package lab1;

import java.util.List;
import java.util.LinkedList;

public class Plant implements Runnable {
    // How long do we want to run the juice processing
    public static final long PROCESSING_TIME = 5 * 500;
    private static final int NUM_PLANTS = 2; // number of plants in this program
    private static final int NUM_WORKER = 2; 
 
    
    //The following is all class variables, they make the code work. Without them, the code is nothing.
    public final int ORANGES_PER_BOTTLE = 3; 
    public final Thread thread; 
    private int orangesProvided; 
    private int orangesProcessed; 
    private volatile boolean timeToWork; 
    private Worker[] workers; 
    //Linked list for keeping oranges stored.
    private final List<Orange> orangePile = new LinkedList<Orange>(); 
    
    
    public static void main(String[] args) {
        // Create and starts up the plant threads
        Plant[] plants = new Plant[NUM_PLANTS];
        for (int i = 0; i < NUM_PLANTS; i++) {
            plants[i] = new Plant(1);
            plants[i].startPlant();
        }
        
        // This delays so that the plants have enough time to complete processes so that a race does not happen. A race could cause great problems
        delay(PROCESSING_TIME, "Plant malfunction");

        // The for loop stops the plant
        for (Plant p : plants) {
            p.stopPlant();
        }
        
        // The following has a join statement. The join statement waits for the thread to die before it activates.
        // The interruptedException is there if a thread interrupts the current thread.
        for (Plant p : plants) { 
            try {
                p.thread.join();
            } catch (InterruptedException e) {
                System.err.println(p.thread.getName() + " stop malfunction");
            }
        }
        
        // Summarize the results
        // Below will set all the values to 0. This is for when the program is run again and needs to get rid of the old values
        int totalProvided = 0;
        int totalProcessed = 0;
        int totalBottles = 0;
        int totalWasted = 0;
        // The following goes through both plants and goes through all the methods below so that they can all be updated for each Plant
        for (Plant p : plants) {
            totalProvided += p.getProvidedOranges();
            totalProcessed += p.getProcessedOranges();
            totalBottles += p.getBottles();
            totalWasted += p.getWaste();
        }
        // the code below prints to the Console some of what the program does. such as how many oranges were given and how many were processed. As well as how many bottles we got from teh oranges and how many were wasted
        System.out.println("Total provided/processed = " + totalProvided + "/" + totalProcessed);
        System.out.println("Created " + totalBottles +
                           ", wasted " + totalWasted + " oranges");
    }
    // This delay method runs while plants are working, this is done so that there are no race problems so that everything runs smoothly
    private static void delay(long time, String errMsg) {
        long sleepTime = Math.max(1, time);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.err.println(errMsg);
        }
    }
    /**
     * Below is the Constructor for the two plants including how many oranges are provided/processed and it also creates the worker threads who work at the plant 
     */
    Plant(int threadNum) {
        orangesProvided = 0;
        orangesProcessed = 0;
        thread = new Thread(this, "Plant[" + threadNum + "]");
        workers = new Worker[NUM_WORKER]; 
        for(int i = 0; i < NUM_WORKER; i++) {
            workers[i] = new Worker(i, this);
        }
    }
    
    /**
     * @return Orange
     * @desc This is the method to be called by the worker threads who want a orange which it will either create or give one that is already created to the worker
     *  It also synchronizes so that it avoids multithreading issues
     */
    public synchronized Orange getOrange() { 
        int size = getPileSize();
        if(size > 0) {
            return orangePile.remove(size-1);
        } else {
            incProvidedOranges();
            return new Orange();
        }
    }
    
    // This starts the Plant threads and also starts the worker threads as well. This is different from the creation above because now they are starting their tasks
    public void startPlant() {
        timeToWork = true;
        for(Worker w:workers) { 
            w.startWorking();
        }
        thread.start();
    }
    
    // The method below is the method that is called to stop the worker threads from working, this is usually when they finish their tasks
    public synchronized void stopPlant() {
    	for(Worker w : workers) { 
            w.stopWorking();
        }
        timeToWork = false;
    }
    
    //Below is the run method, it starts the program with processing oranges and stopping it. It also only runs when it is allowed dto work in the allotted time
    public void run() {
        System.out.print(Thread.currentThread().getName() + " Processing oranges");
        while (timeToWork) { 
            addOrange(new Orange());
            incProvidedOranges(); 
        }
        // this joins the workers so that the plants do not stop before the workers stop
        try { 
            for(Worker w : workers) {
                w.thread.join();
            }
        } catch (InterruptedException e) {
            System.err.println(thread.getName() + " stop malfunction");
        }
        System.out.println(Thread.currentThread().getName() + " Done");
    }
    
    
    // All the below are synchronized get and add methods to make sure everything is working correctly and how it should be
    public synchronized void addOrange(Orange o) { 
        orangePile.add(o);
    }

    public synchronized void incProcessedOranges() {
        orangesProcessed++;
    }

    public synchronized void incProvidedOranges() {
        orangesProvided++;
    }

    private synchronized int getPileSize() {
        return orangePile.size();
    }
    
    public int getProvidedOranges() {
        return orangesProvided;
    }

    public int getProcessedOranges() {
        return orangesProcessed;
    }

    public int getBottles() {
        return orangesProcessed / ORANGES_PER_BOTTLE;
    }

    public int getWaste() {
        return orangesProcessed % ORANGES_PER_BOTTLE;
    }
}
