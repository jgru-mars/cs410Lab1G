package lab1;

// This entire class I got from Matt's code with his permission. 


public class Worker implements Runnable{ // got this entire class from Matt
    // Here are three class variables
    private volatile boolean working; 
    public final Thread thread; 
    public final Plant plant; //this line is for the Plant that the worker is working for

    // Below is the Constructor for the worker, uses same naming convention as Plant + the plant the worker is from
    Worker(int threadNum, Plant p) {
        thread = new Thread(this, "Worker ["+threadNum+"]" + " " + p.thread.getName());
        this.plant = p;
        working = false;
    }
    // This entire class I got from Matt's code with his permission. 
    
    // this makes the workers start working
    public void startWorking() {
        working = true;
        thread.start();
    }

    /**
     * The method below is for while the worker is working they grab a orange from the plant they are working at. They then process the orange
     * that they have. Then it goes to the if statement where if the orange is processed and not thrown away then it is added the inProcessedOranges
     */
     // This entire class I got from Matt's code with his permission. 
    public void run() {
        while (working) {
            Orange o = plant.getOrange();
            System.out.println(thread.getName() + " " + o.getState() + " orange");
            o.runProcess();
            if(o.getState() == Orange.State.Processed) {
                plant.incProcessedOranges();
            } else {
                plant.addOrange(o);
            }
        }
        System.out.println(thread.getName() + " Done"); // prints to the console that the worker and plant are both done
    }
    // This entire class I got from Matt's code with his permission. 

    // below is the method to stop the workers at the plants from working.
    public void stopWorking() {
        working = false;
    }
}
//As stated above. I got this entire class from Matt's code.