package ca.cmpt276.restauranthealthinspection.model;

public class Violation {
    private int number;
    private String description;
    private boolean isCritical;
    private boolean isRepeat;

    public Violation(int number, String description, boolean isCritical, boolean isRepeat) {
        this.number = number;
        this.description = description;
        this.isCritical = isCritical;
        this.isRepeat = isRepeat;
    }
}
