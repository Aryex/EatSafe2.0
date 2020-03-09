package ca.cmpt276.restauranthealthinspection.model;

import java.io.Serializable;

/**
 * Represents the violations in an inspection.
 */
public class Violation implements Serializable {

    private int number;

    private String criticalStatus;

    private String description;

    private String repeatStatus;

    public static final String CRITICAL_STATUS = "Critical";

    public static final String NON_CRITICAL_STATUS = "Not Critical";

    Violation(int number, String criticalStatus, String description, String repeatStatus) {
        this.number = number;
        this.criticalStatus = criticalStatus;
        this.description = description;
        this.repeatStatus = repeatStatus;
    }

    public int getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }

    public String getCriticalStatus() {
        return criticalStatus;
    }

    public String getRepeatStatus() {
        return repeatStatus;
    }

    @Override
    public String toString() {
        return "\n\t\tViolation{" +
                "number=" + number +
                ", criticalStatus='" + criticalStatus + '\'' +
                ", description='" + description + '\'' +
                ", repeatStatus='" + repeatStatus + '\'' +
                '}';
    }
}