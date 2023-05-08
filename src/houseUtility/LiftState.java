package houseUtility;

public enum LiftState {
    UP(1), DOWN(-1), NONE(0), FREE(0), BUSY(2);

    private int value;

    private LiftState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
