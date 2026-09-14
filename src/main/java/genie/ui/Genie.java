package genie.ui;


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
            if (userInput.equalsIgnoreCase("bye")) {
                return "Bye! See you again soon!";
            } else if (userInput.equalsIgnoreCase("list")) {
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
