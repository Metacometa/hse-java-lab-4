package houseUtility;

import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

/**
 * Class house controls application and lifts
 * Separated by three threads that:
 * - Generates applications
 * - Distribute applications
 * - Move lifts and prints the house in a successful case
 */
public class House {
    private Lift firstLift;
    private Lift secondLift;

    /**
     * There are upward and downward applications that is not distributed yet
     */
    private Applications undistributedApplications;

    /**
     * The number of levels in the house
     */
    private int levels;

    /*
     * @param levels the number of levels in the new house
     */
    public House(int levels) {
        this.levels = levels;
        firstLift = new Lift();
        secondLift = new Lift();
        undistributedApplications = new Applications();
    }

    /**
     * Manage firstly upward application and downwad application after
     */
    public void manageApplications() {
        synchronized(undistributedApplications) {
            if (!undistributedApplications.upwardApplications.isEmpty()) {
                distributeApplications(undistributedApplications.upwardApplications.iterator(), firstLift, firstLift.upwardApplications, secondLift, secondLift.upwardApplications, LiftState.UP);
            }
            if (!undistributedApplications.downwardApplications.isEmpty()){
                distributeApplications(undistributedApplications.downwardApplications.iterator(), firstLift, firstLift.downwardApplications, secondLift, secondLift.downwardApplications, LiftState.DOWN);
            }
        }
    }

    /**
     * Moves first and second lift and prints house in case of successful moving
     */
    public void manageLifts() {
        synchronized(undistributedApplications) {
            boolean firstLiftMoved = moveLift(firstLift);
            boolean secondLiftMoved = moveLift(secondLift);
            if (firstLiftMoved || secondLiftMoved) {
                print();
            }
        }
    }

    /**
     * Create an application on random level with random direction
     * If the first level is randomized, direction is upward
     * If the last level is randomized, direction is downward
     */
    public void generateApplication() {
        synchronized(undistributedApplications) {
            Random r = new Random();

            int direction = r.nextInt(2) + 1;
            int application = r.nextInt(levels) + 1;

            if (application == 1) {
                System.out.print("Generated application: ");
                undistributedApplications.upwardApplications.add(application);
                System.out.println("↑" + application);
            }
            else if (application == levels) {
                System.out.print("Generated application: ");
                undistributedApplications.downwardApplications.add(application);
                System.out.println("↓" + application);
            }
            else if ((direction % 2 == 0) && isThisApplicationContained(application, "downward")) {
                System.out.print("Generated application: ");
                undistributedApplications.downwardApplications.add(application);
                System.out.println("↓" + application);
            }
            else if (isThisApplicationContained(application, "upward")) {
                System.out.print("Generated application: ");
                undistributedApplications.upwardApplications.add(application);
                System.out.println("↑" + application);
            }
            else {
                return;
            }

            undistributedApplications.notify();
        }
    }

    /**
     * Distribute undistributed applications between upward and downward application of first and second lifts
     * @param it iterator to downward or upward undistributed applications
     * @param a first lift
     * @param applicationsA upward of downward application of first lift
     * @param b second lift
     * @param applicationsB upward of downward application of second lift
     * @param state state according to upward of downward applications are taken
     */
    private void distributeApplications(Iterator<Integer> it, Lift a, TreeSet<Integer> applicationsA, Lift b, TreeSet<Integer> applicationsB, LiftState state) {
        while (it.hasNext()) {
            Integer application = it.next();

            Lift closestLift;
            Lift furthestLift;
            TreeSet<Integer> closestLiftApplications;
            TreeSet<Integer> furthestLiftApplications;

            if (getDistance(a.level, application) <= getDistance(b.level, application)) {
                closestLift = a;
                furthestLift = b;
                closestLiftApplications = applicationsA;
                furthestLiftApplications = applicationsB;
            }
            else {
                closestLift = b;
                furthestLift = a;
                closestLiftApplications = applicationsB;
                furthestLiftApplications = applicationsA;
            }

            if (closestLift.state == LiftState.FREE) {
                closestLiftApplications.add(application);
                closestLift.state = state;
                it.remove();
            }
            else if (closestLift.state == state) {
                if ((closestLift.level < closestLiftApplications.first() && closestLift.level <= application) || (application <= closestLift.level && closestLiftApplications.last() < closestLift.level)) {
                    closestLiftApplications.add(application);
                    it.remove();
                }
            }
            else if (furthestLift.state == LiftState.FREE) {
                furthestLiftApplications.add(application);
                furthestLift.state = state;
                it.remove();
            }
            else if (furthestLift.state == state) {
                if ((furthestLift.level < furthestLiftApplications.first() && furthestLift.level <= application) || (application <= furthestLift.level && furthestLiftApplications.last() < furthestLift.level)) {
                    furthestLiftApplications.add(application);
                    it.remove();
                }
            }
        }
    }

    /**
     * Move lift according to its handling applications. Makes state "FREE" when applications are empty
     * @param lift to be moved
     * @return if lift was moved
     */
    private boolean moveLift(Lift lift) {
        if (lift.upwardApplications.isEmpty() && lift.downwardApplications.isEmpty()) {
            lift.state = LiftState.FREE;
            return false;
        }
        else if (!lift.upwardApplications.isEmpty() && lift.state == LiftState.UP) {
            if (lift.upwardApplications.first() == lift.level) {
                lift.upwardApplications.remove(lift.level);
            }
            if (lift.upwardApplications.isEmpty()) {
                lift.level++;
                lift.state = LiftState.FREE;
            }
            else {
                lift.level += Math.signum(lift.upwardApplications.first() - lift.level);
            }
        }
        else if (!lift.downwardApplications.isEmpty() && lift.state == LiftState.DOWN){
            if (lift.downwardApplications.last() == lift.level) {
                lift.downwardApplications.remove(lift.level);
            }

            if (lift.downwardApplications.isEmpty()) {
                lift.level--;
                lift.state = LiftState.FREE;
            }
            else {
                lift.level += Math.signum(lift.downwardApplications.last() - lift.level);
            }
        }
        else {
            return false;
        }
        return true;
    }


    /**
     * @param application a new application
     * @param direction direction of a new application
     * @return does such appliaction exists
     */
    private boolean isThisApplicationContained(int application, String direction) {
        switch(direction) {
            case "upward":
                return !firstLift.upwardApplications.contains(application) && !secondLift.upwardApplications.contains(application);
            case "downward":
                return !firstLift.downwardApplications.contains(application) && !secondLift.downwardApplications.contains(application);
            default:
                return false;
        }
    }

    /**
     * Prints house as ascii-art
     */
    private void print() {
        synchronized (undistributedApplications) {
            for (int i = levels; i > 0; --i) {
                if (firstLift.level == i) {
                    System.out.print("E");
                }
                else {
                    System.out.print("_");
                }

                System.out.print("|");

                if (undistributedApplications.upwardApplications.contains(i) || firstLift.upwardApplications.contains(i) || secondLift.upwardApplications.contains(i)) {
                    System.out.print("↑");
                }
                else {
                    System.out.print("_");
                }
                if (undistributedApplications.downwardApplications.contains(i) || firstLift.downwardApplications.contains(i) || secondLift.downwardApplications.contains(i)) {
                    System.out.print("↓");
                }
                else {
                    System.out.print("_");
                }

                if (undistributedApplications.contains(i) || firstLift.contains(i) || secondLift.contains(i)) {
                    if (i < 10) {
                        System.out.print("_");
                    }

                    System.out.print(i);
                }
                else {
                    System.out.print("__");
                }

                System.out.print("|");

                if (secondLift.level == i) {
                    System.out.print("E");
                }
                else {
                    System.out.print("_");
                }

                System.out.println();
            }
            System.out.println();
        }
    }

    /**
     * @param a level
     * @param b level
     * @return distance between levels
     */
    private int getDistance(int a, int b) {
        return Math.abs(a - b);
    }


}
