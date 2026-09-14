package genie.ui;

/** Represents a generic task with a description and completion status. */
class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Initializes a new Task.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks the task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks the task as incomplete. */
    public void markAsUndone() {
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "[X]" : "[ ]");
    }

    public String getName() {
        return this.description;
    }

    /**
     * Returns the string representation of the task for saving to a file.
     *
     * @return Formatted string for file storage.
     */
    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }
}
