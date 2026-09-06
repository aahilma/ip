package genie.ui;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * Represents the main entry point for the Genie chatbot.
 * Handles user interactions, task management, and file reading/writing.
 */

public class Genie extends Application {
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
    private final Storage storage = new Storage(FILE_PATH);
    private final ArrayList<Task> tasks = storage.load();

    /** Starts the JavaFX desktop application. */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Genie.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setGenie(this);

            Scene scene = new Scene(root);
            stage.setTitle("Genie");
            stage.setMinHeight(450);
            stage.setMinWidth(420);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the Genie GUI", e);
        }
    }

    /** Runs the original command-line interface for users who invoke this method directly. */
    private void runCli() {

        String divider = "    ____________________________________________________________";
        System.out.println(divider);
        System.out.println("     Hello! I'm Genie\n     What can I do for you?");
        System.out.println(divider);



        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(FILE_PATH);
        ArrayList<Task> tasks = storage.load();

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
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println("     " + (i + 1) + "." + tasks.get(i).toString());
                }

            } else if (commands[0].equalsIgnoreCase("mark")) {
                int i = Integer.parseInt(commands[1]) - 1;
                tasks.get(i).markAsDone();
                storage.save(tasks);
                System.out.println("     Nice! I have marked this task as done:\n");
                System.out.println("    " + tasks.get(i).toString());
            } else if (commands[0].equalsIgnoreCase("unmark")) {
                int i = Integer.parseInt(commands[1]) - 1;
                tasks.get(i).markAsUndone();
                storage.save(tasks);
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
                storage.save(tasks);


                System.out.println("     Got it. I've added this task:");
                System.out.println("       " + tasks.get(tasks.size() - 1).toString());
                System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
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
                    storage.save(tasks);

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
                    storage.save(tasks);

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
                storage.save(tasks);


                System.out.println("     Noted. I've removed this task:");
                System.out.println("       " + removedTask.toString());
                System.out.println("     Now you have " + tasks.size() + " tasks in the list.");
            } else if (commands[0].equalsIgnoreCase("find")) {
                if (commands.length == 1) {
                    System.out.println("     OOPS!!! Please provide a keyword to search for.");
                    continue;
                }

                String keyword = userInput.substring("find".length()).trim();

                System.out.println("     Here are the matching tasks in your list:");

                int matchCount = 0;
                for (int i = 0; i < tasks.size(); i++) {
                    Task currentTask = tasks.get(i);
                    // Check if the task's description contains the keyword
                    if (currentTask.getName().contains(keyword)) {
                        matchCount++;
                        System.out.println("     " + matchCount + "." + currentTask.toString());
                    }
                }

                if (matchCount == 0) {
                    System.out.println("     No tasks matched your search.");
                }

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
     * Processes one command and returns the response for display in either interface.
     * The command syntax and task operations are shared by the CLI and GUI.
     *
     * @param userInput command entered by the user
     * @return response text
     */
    String processCommand(String userInput) {
        String[] commands = userInput.split(" ");
        try {
            if (userInput.equalsIgnoreCase("list")) {
                StringBuilder result = new StringBuilder("Here are your tasks:");
                for (int i = 0; i < tasks.size(); i++) {
                    result.append("\n").append(i + 1).append(".").append(tasks.get(i));
                }
                return result.toString();
            } else if (commands[0].equalsIgnoreCase("mark") || commands[0].equalsIgnoreCase("unmark")) {
                int index = Integer.parseInt(commands[1]) - 1;
                if (commands[0].equalsIgnoreCase("mark")) {
                    tasks.get(index).markAsDone();
                } else {
                    tasks.get(index).markAsUndone();
                }
                storage.save(tasks);
                return "Nice! I have updated this task:\n" + tasks.get(index);
            } else if (commands[0].equalsIgnoreCase("delete")) {
                Task removed = tasks.remove(Integer.parseInt(commands[1]) - 1);
                storage.save(tasks);
                return "Noted. I've removed this task:\n" + removed;
            } else if (commands[0].equalsIgnoreCase("find")) {
                String keyword = userInput.substring(4).trim();
                StringBuilder result = new StringBuilder("Here are the matching tasks:");
                int count = 0;
                for (Task task : tasks) {
                    if (task.getName().contains(keyword)) {
                        result.append("\n").append(++count).append(".").append(task);
                    }
                }
                return count == 0 ? "No tasks matched your search." : result.toString();
            }
            // Creation commands retain the original parser and validation messages.
            if (commands[0].equalsIgnoreCase("todo")) {
                if (commands.length == 1) {
                    return "OOPS!!! The description of a todo cannot be empty.";
                }
                tasks.add(new ToDo(userInput.substring(5).trim()));
            } else if (commands[0].equalsIgnoreCase("deadline")) {
                String[] parts = userInput.substring(9).trim().split(" /by ");
                tasks.add(new Deadline(parts[0], LocalDateTime.parse(parts[1].trim(), INPUT_FORMAT)));
            } else if (commands[0].equalsIgnoreCase("event")) {
                String[] parts = userInput.substring(6).trim().split(" /from ");
                String[] times = parts[1].split(" /to ");
                tasks.add(new Event(parts[0], LocalDateTime.parse(times[0].trim(), INPUT_FORMAT),
                        LocalDateTime.parse(times[1].trim(), INPUT_FORMAT)));
            } else {
                return "OOPS!!! I'm sorry, but I don't know what that means :-(";
            }
            storage.save(tasks);
            return "Got it. I've added this task:\n" + tasks.get(tasks.size() - 1)
                    + "\nNow you have " + tasks.size() + " tasks in the list.";
        } catch (DateTimeParseException e) {
            return "OOPS!!! Please use the format: d/M/yyyy HHmm (e.g., 2/12/2019 1800)";
        } catch (RuntimeException e) {
            return "OOPS!!! Please check the command and task number.";
        }
    }

    /** Launches JavaFX. */
    public static void main(String[] args) {
        launch(args);
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
        return "E | " + super.toFileFormat() + " | " + from.format(Genie.INPUT_FORMAT)
                + " | " + to.format(Genie.INPUT_FORMAT);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(Genie.OUTPUT_FORMAT)
                + " to: " + to.format(Genie.OUTPUT_FORMAT) + ")";
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

/**
 * Deals with loading tasks from the file and saving tasks in the file.
 */
class Storage {
    private final String filePath;

    /**
     * Initializes a Storage handler with the specified file path.
     *
     * @param filePath Path of the file where tasks are saved.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the local file system.
     *
     * @return List of tasks loaded from the file.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return tasks;
            }

            Scanner fileScanner = new Scanner(f);
            while (fileScanner.hasNext()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(" \\| ");
                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                Task t = null;

                if (type.equals("T")) {
                    t = new ToDo(description);
                } else if (type.equals("D")) {
                    LocalDateTime by = LocalDateTime.parse(parts[3], Genie.INPUT_FORMAT);
                    t = new Deadline(description, by);
                } else if (type.equals("E")) {
                    LocalDateTime from = LocalDateTime.parse(parts[3], Genie.INPUT_FORMAT);
                    LocalDateTime to = LocalDateTime.parse(parts[4], Genie.INPUT_FORMAT);
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
        return tasks;
    }

    /**
     * Saves the current list of tasks to the local file system.
     *
     * @param tasks List of tasks to be saved.
     */
    public void save(ArrayList<Task> tasks) {
        try {
            File dir = new File("./data");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            FileWriter fw = new FileWriter(filePath);
            for (Task t : tasks) {
                fw.write(t.toFileFormat() + System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("     Error saving tasks: " + e.getMessage());
        }
    }
}
