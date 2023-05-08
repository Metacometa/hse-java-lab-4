import houseUtility.*;

public class Main {

    public static void main(String[] args) {
        int levels = 10;
        int applicationsDelay = 2000;
        int printDelay = 0;

        House house = new House(levels);

        HouseThread lift1 = new HouseThread(house::moveFirstLift, 1000);
        lift1.start();

        HouseThread lift2 = new HouseThread(house::moveSecondLift, 1000);
        lift2.start();

        HouseThread applicationsGenerator = new HouseThread(house::generateApplication, applicationsDelay);
        applicationsGenerator.start();

        HouseThread applicationManager = new HouseThread(house::manageApplications, 100);
        applicationManager.start();

        HouseThread printing = new HouseThread(house::print, printDelay);
        printing.start();
    }
}