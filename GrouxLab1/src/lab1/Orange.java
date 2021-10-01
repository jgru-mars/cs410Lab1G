package lab1;

// this class is for all the oranges that are being processed by the plants and workers. It includes the methods for fetching, peeling, squeezing, and bottling the oranges
public class Orange {
    public enum State {
        Fetched(15),
        Peeled(38),
        Squeezed(29),
        Bottled(17),
        Processed(1);

    	//the line below is for changing the state that the process is in
        private static final int finalIndex = State.values().length - 1;

        
        final int timeToComplete;

        State(int timeToComplete) {
            this.timeToComplete = timeToComplete;
        }

        // this happens after one state of the orange process is complete and it moves to the next state
        State getNext() {
            int currIndex = this.ordinal();
            if (currIndex >= finalIndex) {
                throw new IllegalStateException("Already at final state");
            }
            return State.values()[currIndex + 1];
        }
    }

    private State state;

    // this method is the first state that the orange can be in and is when the orange first arrives to go through the process
    public Orange() {
        state = State.Fetched;
        doWork();
    }

    public State getState() {
        return state;
    }

    public void runProcess() {
        // Don't attempt to process an already completed orange
        if (state == State.Processed) {
            throw new IllegalStateException("This orange has already been processed");
        }
        //calls the doWork function below
        doWork();
        state = state.getNext();
    }
    
    private void doWork() {
        // below will stop the class program when the selected time has been done.
        try {
            Thread.sleep(state.timeToComplete);
        } catch (InterruptedException e) {
            System.err.println("Incomplete orange processing, juice may be bad");
        }
    }
}