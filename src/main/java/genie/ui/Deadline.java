package genie.ui;

import java.time.LocalDateTime;

/** Represents a task that needs to be done before a specific date or time. */
class Deadline extends Task {
    protected LocalDateTime by;

    /**
     * Initializes a new Deadline task.
     *
     * @param description Description of the deadline.
     * @param by          The deadline date or time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        assert by != null : "Deadline date and time must not be null";
        this.by = by;
    }

    /** Updates the deadline date and time. */
    public void setBy(LocalDateTime by) {
        assert by != null : "Deadline date and time must not be null";
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by.format(Genie.INPUT_FORMAT);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(Genie.OUTPUT_FORMAT) + ")";
    }
}
