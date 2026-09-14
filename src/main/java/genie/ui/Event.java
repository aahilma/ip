package genie.ui;

import java.time.LocalDateTime;

/** Represents a task that starts and ends at specific times. */
class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Initializes a new Event task.
     *
     * @param description Description of the event.
     * @param from        Start date or time of the event.
     * @param to          End date or time of the event.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /** Updates the event start date and time. */
    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    /** Updates the event end date and time. */
    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from.format(Genie.INPUT_FORMAT)
                + " | " + to.format(Genie.INPUT_FORMAT);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(Genie.OUTPUT_FORMAT)
                + " to: " + to.format(Genie.OUTPUT_FORMAT) + ")";
    }
}
