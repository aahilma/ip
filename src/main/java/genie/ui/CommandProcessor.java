package genie.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/** Processes Genie commands and updates the task list. */
class CommandProcessor {
    private final Storage storage;
    private final ArrayList<Task> tasks;

    /**
     * Creates a command processor backed by the specified task file.
     *
     * @param filePath path of the file used to persist tasks
     */
    CommandProcessor(String filePath) {
        storage = new Storage(filePath);
        tasks = storage.load();
    }

    /**
     * Processes one command and returns the response for display in the GUI.
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
            } else if (commands[0].equalsIgnoreCase("mark")
                    || commands[0].equalsIgnoreCase("unmark")) {
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

            if (commands[0].equalsIgnoreCase("todo")) {
                if (commands.length == 1) {
                    return "OOPS!!! The description of a todo cannot be empty.";
                }
                tasks.add(new ToDo(userInput.substring(5).trim()));
            } else if (commands[0].equalsIgnoreCase("deadline")) {
                String[] parts = userInput.substring(9).trim().split(" /by ");
                tasks.add(new Deadline(parts[0], LocalDateTime.parse(parts[1].trim(),
                        Genie.INPUT_FORMAT)));
            } else if (commands[0].equalsIgnoreCase("event")) {
                String[] parts = userInput.substring(6).trim().split(" /from ");
                String[] times = parts[1].split(" /to ");
                tasks.add(new Event(parts[0], LocalDateTime.parse(times[0].trim(), Genie.INPUT_FORMAT),
                        LocalDateTime.parse(times[1].trim(), Genie.INPUT_FORMAT)));
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
}
