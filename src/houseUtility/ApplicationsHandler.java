package houseUtility;

import java.util.TreeSet;

public class ApplicationsHandler {
    public TreeSet<Integer> upwardApplications;
    public TreeSet<Integer> downwardApplications;

    ApplicationsHandler() {
        upwardApplications = new TreeSet<>();
        downwardApplications = new TreeSet<>();
    }

    public void handlePassenger(Lift source) {
        if (source.state == LiftState.UP) {
            takePassenger(source.level, upwardApplications);
        }
        else if (source.state == LiftState.DOWN) {
            takePassenger(source.level, downwardApplications);
        }
    }

    void takePassenger(int level, TreeSet<Integer> applications) {
        if (applications.contains(level)) {
            applications.remove(level);
        }
    }

    boolean contains(int level) {
        return upwardApplications.contains(level) || downwardApplications.contains(level);
    }
}
