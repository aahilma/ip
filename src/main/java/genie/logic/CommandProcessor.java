package genie.logic;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import genie.model.Deadline;
import genie.model.Event;
import genie.model.Task;
import genie.model.ToDo;
import genie.storage.Storage;
import genie.util.DateFormats;

/** Processes Genie commands and updates the task list. */
public class CommandProcessor {
    private static final String DATE_FORMAT_ERROR =
            "OOPS!!! Please use the format: d/M/yyyy HHmm (e.g., 2/12/2019 1800)";
    private final Storage storage;
    private final ArrayList<Task> tasks;

    /**
     * Creates a command processor backed by the specified task file.
     *
     * @param filePath Path of the file used to persist tasks.
     */
    public CommandProcessor(String filePath) {
        storage = new Storage(filePath);
        tasks = storage.load();
        assert tasks != null : "Storage must return a task list";
    }

    /**
     * Processes one command and returns the response for display in the GUI.
     *
     * @param userInput Command entered by the user.
     * @return Response text.
     */
    public String processCommand(String userInput) {
        String[] commands = userInput.split(" ");
        try {
            if (userInput.equalsIgnoreCase("bye")) {
                return "Bye! See you again soon!";
            } else if (userInput.equalsIgnoreCase("list")) {
                return listTasks();
            } else if (commands[0].equalsIgnoreCase("mark")
                    || commands[0].equalsIgnoreCase("unmark")) {
                return updateTaskStatus(commands);
            } else if (commands[0].equalsIgnoreCase("delete")) {
                return deleteTask(commands);
            } else if (commands[0].equalsIgnoreCase("find")) {
                return findTasks(userInput);
            } else if (commands[0].equalsIgnoreCase("edit")) {
                return editTask(userInput);
            }

            return addTask(userInput, commands);
        } catch (DateTimeParseException e) {
            return DATE_FORMAT_ERROR;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return "OOPS!!! Please check the command and task number.";
        }
    }

    /** Returns all tasks with their list positions. */
    private String listTasks() {
        StringBuilder result = new StringBuilder("Here are your tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            result.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return result.toString();
    }

    /** Returns a response after marking or unmarking a task. */
    private String updateTaskStatus(String[] commands) {
        int index = Integer.parseInt(commands[1]) - 1;
        Task task = tasks.get(index);
        if (commands[0].equalsIgnoreCase("mark")) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
        storage.save(tasks);
        return "Nice! I have updated this task:\n" + task;
    }

    /** Returns a response after deleting a task. */
    private String deleteTask(String[] commands) {
        Task removed = tasks.remove(Integer.parseInt(commands[1]) - 1);
        storage.save(tasks);
        return "Noted. I've removed this task:\n" + removed;
    }

    /** Returns the tasks whose descriptions contain the search keyword. */
    private String findTasks(String userInput) {
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

    /** Adds a todo, deadline, or event and returns the resulting response. */
    private String addTask(String userInput, String[] commands) {
        if (commands[0].equalsIgnoreCase("todo")) {
            if (commands.length == 1) {
                return "OOPS!!! The description of a todo cannot be empty.";
            }
            tasks.add(new ToDo(userInput.substring(5).trim()));
        } else if (commands[0].equalsIgnoreCase("deadline")) {
            String[] parts = userInput.substring(9).trim().split(" /by ");
            tasks.add(new Deadline(parts[0], LocalDateTime.parse(parts[1].trim(),
                    DateFormats.INPUT_FORMAT)));
        } else if (commands[0].equalsIgnoreCase("event")) {
            String[] parts = userInput.substring(6).trim().split(" /from ");
            String[] times = parts[1].split(" /to ");
            tasks.add(new Event(parts[0], LocalDateTime.parse(times[0].trim(),
                    DateFormats.INPUT_FORMAT), LocalDateTime.parse(times[1].trim(),
                    DateFormats.INPUT_FORMAT)));
        } else {
            return "OOPS!!! I'm sorry, but I don't know what that means :-(";
        }
        storage.save(tasks);
        return "Got it. I've added this task:\n" + tasks.get(tasks.size() - 1)
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Updates one field of an existing task without changing its other fields.
     *
     * @param userInput Complete edit command.
     * @return Response text.
     */
    private String editTask(String userInput) {
        String[] parts = userInput.trim().split("\\s+", 4);
        if (parts.length < 4) {
            return "OOPS!!! Usage: edit <task number> <field> <new value>.";
        }

        int index;
        try {
            index = Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException e) {
            return "OOPS!!! Please provide a valid task number.";
        }

        if (index < 0 || index >= tasks.size()) {
            return "OOPS!!! Please provide a valid task number.";
        }

        Task task = tasks.get(index);
        String field = parts[2].toLowerCase();
        String value = parts[3].trim();

        try {
            switch (field) {
                case "description":
                    task.setDescription(value);
                    break;
                case "by":
                    if (!(task instanceof Deadline)) {
                        return "OOPS!!! The 'by' field can only be edited for deadlines.";
                    }
                    LocalDateTime deadline = LocalDateTime.parse(value, DateFormats.INPUT_FORMAT);
                    Deadline deadlineTask = (Deadline) task;
                    deadlineTask.setBy(deadline);
                    break;
                case "from":
                    if (!(task instanceof Event)) {
                        return "OOPS!!! The 'from' field can only be edited for events.";
                    }
                    LocalDateTime startTime = LocalDateTime.parse(value, DateFormats.INPUT_FORMAT);
                    Event startEvent = (Event) task;
                    startEvent.setFrom(startTime);
                    break;
                case "to":
                    if (!(task instanceof Event)) {
                        return "OOPS!!! The 'to' field can only be edited for events.";
                    }
                    LocalDateTime endTime = LocalDateTime.parse(value, DateFormats.INPUT_FORMAT);
                    Event endEvent = (Event) task;
                    endEvent.setTo(endTime);
                    break;
                default:
                    return "OOPS!!! Supported fields are description, by, from, and to.";
            }
        } catch (DateTimeParseException e) {
            return DATE_FORMAT_ERROR;
        }

        storage.save(tasks);
        return "Got it. I've updated this task:\n" + task;
    }
}
