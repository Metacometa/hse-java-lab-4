package houseUtility;

import java.util.TreeSet;

public class Lift {
    LiftState state;
    int level;

    TreeSet<Integer> upwardApplications;
    TreeSet<Integer> downwardApplications;

    public Lift() {
        state = LiftState.FREE;
        level = 1;
        upwardApplications = new TreeSet<>();
        downwardApplications = new TreeSet<>();
    }

    public boolean contains(int level) {
        return upwardApplications.contains(level) || downwardApplications.contains(level);
    }
}


