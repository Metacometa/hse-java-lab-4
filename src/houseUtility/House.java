package houseUtility;

import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

@FunctionalInterface

interface Comparator {
    boolean compare(int a, int b);
}

public class House {
    Lift firstLift;
    Lift secondLift;

    /**
     * negative value - the downward application
     * positive value - the upward application
     */
    ApplicationsHandler applications;

    static Comparator A = (int a, int b) -> a <= b;
    static Comparator B = (int a, int b) -> a >= b;

    int levels;

    public House(int levels) {
        this.levels = levels;
        firstLift = new Lift();
        secondLift = new Lift();
        applications = new ApplicationsHandler();
    }

    public void manageApplications() {
        synchronized(applications) {
            manage(applications.upwardApplications.iterator(), firstLift, firstLift.upwardApplications, secondLift, secondLift.upwardApplications, LiftState.UP, A);
            manage(applications.downwardApplications.iterator(), firstLift, firstLift.downwardApplications, secondLift, secondLift.downwardApplications, LiftState.DOWN, B);
        }
    }

    void manage(Iterator<Integer> it, Lift a, TreeSet<Integer> applicationsA, Lift b, TreeSet<Integer> applicationsB, LiftState state, Comparator c) {
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
            } else {
                closestLift = b;
                furthestLift = a;
                closestLiftApplications = applicationsB;
                furthestLiftApplications = applicationsA;
            }

            if (closestLift.state == state && c.compare(closestLift.level, application)) {
                closestLiftApplications.add(application);
                it.remove();
            } else if (furthestLift.state == state && c.compare(furthestLift.level, application)) {
                furthestLiftApplications.add(application);
                it.remove();
            } else if (closestLift.state == LiftState.FREE) {
                closestLiftApplications.add(application);
                closestLift.state = state;
                it.remove();
            } else if (furthestLift.state == LiftState.FREE) {
                furthestLiftApplications.add(application);
                furthestLift.state = state;
                it.remove();
            }
        }
    }

    boolean moveLift(Lift lift) {
        /*
        System.out.print("Upward: ");
        for (Integer i:lift.upwardApplications) {
            System.out.print(i + "");
        }
        System.out.print("\nDownward: ");
        for (Integer i:lift.downwardApplications) {
            System.out.print(i + "");
        }
        System.out.println();*/

        /*
        подумай

        *
        8
        *
        _
        *
        4
        *
        2
        *

         */
        *?

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

    public void moveFirstLift() {
        synchronized(applications) {
            if (moveLift(firstLift)) {
                System.out.println("Moved first list");
                applications.notify();
            }

        }
    }

    public void moveSecondLift() {
        synchronized(applications) {
            if (moveLift(secondLift)) {
                System.out.println("Moved second list");
                applications.notify();
            }
        }
    }

    public void generateApplication() {
        synchronized(applications) {
            Random r = new Random();

            int direction = r.nextInt(2) + 1;
            int application = r.nextInt(levels) + 1;

            if ((application == 1) && (!firstLift.upwardApplications.contains(application) && !secondLift.upwardApplications.contains(application))) {
                System.out.print("Generated application: ");
                applications.upwardApplications.add(application);
                System.out.println("↑" + application);
            }
            else if ((application == levels) && (!firstLift.downwardApplications.contains(application) && !secondLift.downwardApplications.contains(application))) {
                System.out.print("Generated application: ");
                applications.downwardApplications.add(application);
                System.out.println("↓" + application);
            }
            else if ((direction % 2 == 0) && (!firstLift.downwardApplications.contains(application) && !secondLift.downwardApplications.contains(application))) {
                System.out.print("Generated application: ");
                applications.downwardApplications.add(application);
                System.out.println("↓" + application);
            }
            else if (!firstLift.upwardApplications.contains(application) && !secondLift.upwardApplications.contains(application)) {
                System.out.print("Generated application: ");
                applications.upwardApplications.add(application);
                System.out.println("↑" + application);
            }
        }
    }

    public void print() {
        synchronized (applications) {
            System.out.println("Stages: " + levels);
            for (int i = levels; i > 0; --i) {
                if (firstLift.level == i) {
                    System.out.print("E");
                }
                else {
                    System.out.print("_");
                }

                System.out.print("|");

                if (applications.upwardApplications.contains(i) || firstLift.upwardApplications.contains(i) || secondLift.upwardApplications.contains(i)) {
                    System.out.print("↑");
                }
                else {
                    System.out.print("_");
                }
                if (applications.downwardApplications.contains(i) || firstLift.downwardApplications.contains(i) || secondLift.downwardApplications.contains(i)) {
                    System.out.print("↓");
                }
                else {
                    System.out.print("_");
                }

                if (applications.contains(i) || firstLift.contains(i) || secondLift.contains(i)) {
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

            try {
                applications.wait();
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }

        }
    }

    int findClosestApplication(int level) {
        int closest = levels - 0;
        int nextLevel = level;

        int distance = 0;
        if (applications.upwardApplications.higher(level) != null) {
            distance = getDistance(level, (int)applications.upwardApplications.higher(level));
            getClosestLevel(level, closest, nextLevel, distance);
        }

        if (applications.upwardApplications.lower(level) != null) {
            distance = getDistance(level, (int)applications.upwardApplications.lower(level));
            getClosestLevel(level, closest, nextLevel, distance);
        }

        if (applications.downwardApplications.higher(level) != null) {
            distance = getDistance(level, (int)applications.downwardApplications.higher(level));
            getClosestLevel(level, closest, nextLevel, distance);
        }

        if (applications.downwardApplications.lower(level) != null) {
            distance = getDistance(level, (int)applications.downwardApplications.lower(level));
            getClosestLevel(level, closest, nextLevel, distance);
        }
        return nextLevel;
    }

    //utilities
    void getClosestLevel(int level, int closest, int nextLevel, int distance) {
        if (Math.abs(distance) < closest) {
            nextLevel = level - distance;
            closest = distance;
        }
    }

    int getDistance(int a, int b) {
        return Math.abs(a - b);
    }

    LiftState getDirection(int source) {
        if (source < 0) {
            return LiftState.UP;
        }
        else if (source > 0) {
            return LiftState.DOWN;
        }
        else {
            return LiftState.NONE;
        }
    }

}
