package genie;


import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * Represents the main entry point for the Genie chatbot.
 * Handles user interactions, task management, and file reading/writing.
 */

public class Genie {
    public static final String FILE_PATH = "./data/genie.txt";
    public static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
    public static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a");
    public static final DateTimeFormatter DATE_ONLY_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");
// ...

    /**
     * Starts the chatbot application, loads existing tasks, and processes user commands.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {

        String divider = "    ____________________________________________________________";
        System.out.println(divider);
        System.out.println("     Hello! I'm Genie\n     What can I do for you?");
        System.out.println(divider);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();


        loadTasks(tasks);

        int count = tasks.size();

        while (true) {
            String userInput = scanner.nextLine();
            String[] commands = userInput.split(" ");

            System.out.println(divider);

            if (userInput.equalsIgnoreCase("bye")) {
                System.out.println("     Bye! See you again soon!");
                System.out.println(divider);
                break;

            } else if (userInput.equalsIgnoreCase("list")) {
                System.out.println("     Here are your tasks:");
                for (int i = 0; i < count; i++) {
                    System.out.println("     " + (i + 1) + "." + tasks.get(i).toString());
                }

            } else if (commands[0].equalsIgnoreCase("mark")) {
                int i = Integer.parseInt(commands[1]) - 1;
                tasks.get(i).markAsDone();
                saveTasks(tasks);
                System.out.println("     Nice! I have marked this task as done:\n");
                System.out.println("    " + tasks.get(i).toString());
            } else if (commands[0].equalsIgnoreCase("unmark")) {
                int i = Integer.parseInt(commands[1]) - 1;
                tasks.get(i).markAsUndone();
                saveTasks(tasks);
                System.out.println("     Nice! I have marked this task as undone:\n");
                System.out.println("    " + tasks.get(i).toString());
            } else if (commands[0].equalsIgnoreCase("todo")) {

                if (commands.length == 1) {
                    System.out.println("     OOPS!!! The description of a todo cannot be empty.");
                    continue;
                }

                String description = userInput.substring(5).trim();
                ToDo temp = new ToDo(description);
                tasks.add(temp);
                count++;
                saveTasks(tasks);


                System.out.println("     Got it. I've added this task:");
                System.out.println("       " + tasks.get(count - 1).toString());
                System.out.println("     Now you have " + count + " tasks in the list.");
            } else if (commands[0].equalsIgnoreCase("deadline")) {
                if (commands.length == 1) {
                    System.out.println("     OOPS!!! The description of a deadline cannot be empty.");
                    continue;
                }

                String info = userInput.substring(9).trim();
                String[] parts = info.split(" /by ");

                try {
                    String description = parts[0];
                    LocalDateTime by = LocalDateTime.parse(parts[1].trim(), INPUT_FORMAT);

                    Deadline temp = new Deadline(description, by);
                    tasks.add(temp);
                    saveTasks(tasks);

                    System.out.println("     Got it. I've added this task:");
                    System.out.println("       " + tasks.get(tasks.size() - 1).toString());
                    System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                } catch (DateTimeParseException e) {
                    System.out.println("     OOPS!!! Please use the format: d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
                }

            } else if (commands[0].equalsIgnoreCase("event")) {
                if (commands.length == 1) {
                    System.out.println("     OOPS!!! The description of an event cannot be empty.");
                    continue;
                }

                String info = userInput.substring(6).trim();
                String[] parts = info.split(" /from ");

                try {
                    String description = parts[0];
                    String[] timeParts = parts[1].split(" /to ");
                    LocalDateTime from = LocalDateTime.parse(timeParts[0].trim(), INPUT_FORMAT);
                    LocalDateTime to = LocalDateTime.parse(timeParts[1].trim(), INPUT_FORMAT);

                    Event temp = new Event(description, from, to);
                    tasks.add(temp);
                    saveTasks(tasks);

                    System.out.println("     Got it. I've added this task:");
                    System.out.println("       " + tasks.get(tasks.size() - 1).toString());
                    System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
                } catch (DateTimeParseException e) {
                    System.out.println("     OOPS!!! Please use the format: d/M/yyyy HHmm (e.g., 2/12/2019 1800)");
                }

            } else if (commands[0].equalsIgnoreCase("delete")) {
                if (commands.length == 1) {
                    System.out.println("     OOPS!!! Please provide the task number to delete.");
                    continue;
                }


                int index = Integer.parseInt(commands[1]) - 1;
                Task removedTask = tasks.remove(index);
                count--;
                saveTasks(tasks);


                System.out.println("     Noted. I've removed this task:");
                System.out.println("       " + removedTask.toString());
                System.out.println("     Now you have " + count + " tasks in the list.");
            } else {
                System.out.println("     OOPS!!! I'm sorry, but I don't know what that means :-(");
            }

            if (!userInput.equalsIgnoreCase("bye")) {
                System.out.println(divider);
            }
        }
        scanner.close();
    }

    /**
     * Saves the current list of tasks to the local file system.
     *
     * @param tasks List of tasks to be saved.
     */
    private static void saveTasks(ArrayList<Task> tasks) {
        try {
            // Ensure the directory exists before writing
            File dir = new File("./data");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            FileWriter fw = new FileWriter(FILE_PATH);
            for (Task t : tasks) {
                fw.write(t.toFileFormat() + System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("     Error saving tasks: " + e.getMessage());
        }
    }


    /**
     * Loads tasks from the local file system into the provided list.
     *
     * @param tasks List to store the loaded tasks.
     */
    private static void loadTasks(ArrayList<Task> tasks) {
        try {
            File f = new File(FILE_PATH);
            if (!f.exists()) {
                return; // If the file doesn't exist yet, simply return an empty list
            }

            Scanner fileScanner = new Scanner(f);
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();

                // Using regex " \\| " to split exactly by the pipe character and surrounding spaces
                String[] parts = line.split(" \\| ");
                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                Task t = null;

                if (type.equals("T")) {
                    t = new ToDo(description);
                } else if (type.equals("D")) {
                    LocalDateTime by = LocalDateTime.parse(parts[3], INPUT_FORMAT);
                    t = new Deadline(description, by);
                } else if (type.equals("E")) {
                    LocalDateTime from = LocalDateTime.parse(parts[3], INPUT_FORMAT);
                    LocalDateTime to = LocalDateTime.parse(parts[4], INPUT_FORMAT);
                    t = new Event(description, from, to);
                }

                if (t != null) {
                    if (isDone) {
                        t.markAsDone();
                    }
                    tasks.add(t);
                }
            }
            fileScanner.close();
        } catch (Exception e) {
            System.out.println("     Error loading file: " + e.getMessage());
        }
    }

}



/**
 * Represents a generic task with a description and completion status.
 */
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

    /**
     * Marks the task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as incomplete.
     */
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

/**
 * Represents a task without any specific date or time attached.
 */
class ToDo extends Task {

    /**
     * Initializes a new ToDo task.
     *
     * @param description Description of the task.
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toFileFormat() {
        return "T | " + super.toFileFormat();
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}

/**
 * Represents a task that starts at a specific time and ends at a specific time.
 */
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

    public LocalDateTime getFromDate() {
        return this.from;
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from.format(Genie.INPUT_FORMAT) + " | " + to.format(Genie.INPUT_FORMAT);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(Genie.OUTPUT_FORMAT) + " to: " + to.format(Genie.OUTPUT_FORMAT) + ")";
    }
}

    /**
     * Represents a task that needs to be done before a specific date or time.
     */
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

