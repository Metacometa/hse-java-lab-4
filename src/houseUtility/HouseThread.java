package houseUtility;

public class HouseThread extends Thread {
    Runnable function;
    int delay;

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
