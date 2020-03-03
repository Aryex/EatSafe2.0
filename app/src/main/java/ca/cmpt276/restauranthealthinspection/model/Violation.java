package ca.cmpt276.restauranthealthinspection.model;

import com.opencsv.bean.CsvBindByPosition;

/**
 * Represents the violations in an inspection.
 */
public class Violation {
    @CsvBindByPosition(position = 0)
    private int number;

    @CsvBindByPosition(position = 1)
    private String criticalStatus;

    @CsvBindByPosition(position = 2)
    private String description;

    @CsvBindByPosition(position = 3)
    private String repeatStatus;

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
}