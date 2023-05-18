package houseUtility;

/**
 * Creates thread with one of functions from class House
 * Usually, it
 */
public class HouseThread extends Thread {
    Runnable function;
    int delay;

    /**
     * @param source function that will be run in a new thread
     * @param delay frequency for running function
     */
    public HouseThread(Runnable source, int delay) {
        function = source;
        this.delay = delay;
    }

    @Override
    public void run() {
        while(true) {
            function.run();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
