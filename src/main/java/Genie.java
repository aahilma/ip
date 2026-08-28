import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Genie {
    public static final String FILE_PATH = "./data/genie.txt";
    public static void main(String[] args) {

        String divider = "    ____________________________________________________________";
        System.out.println(divider);
        System.out.println("     Hello! I'm Genie\n     What can I do for you?");
        System.out.println(divider);

        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();
        int count = 0;

        loadTasks(tasks);

        while(true) {
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
            }
            else if (commands[0].equalsIgnoreCase("unmark")) {
                int i = Integer.parseInt(commands[1]) - 1;
                tasks.get(i).markAsUndone();
                saveTasks(tasks);
                System.out.println("     Nice! I have marked this task as undone:\n");
                System.out.println("    " + tasks.get(i).toString());
            }
            else if (commands[0].equalsIgnoreCase("todo")) {

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
            }
            else if (commands[0].equalsIgnoreCase("deadline")) {
                if (commands.length == 1) {
                    System.out.println("     OOPS!!! The description of a deadline cannot be empty.");
                    continue;
                }

                String info = userInput.substring(9).trim();


                String[] parts = info.split(" /by ");
                String description = parts[0];
                String by = parts[1];

                Deadline temp = new Deadline(description, by);
                tasks.add(temp);
                count++;
                saveTasks(tasks);

                System.out.println("     Got it. I've added this task:");
                System.out.println("       " + tasks.get(count - 1).toString());
                System.out.println("     Now you have " + count + " tasks in the list.");

            }
            else if (commands[0].equalsIgnoreCase("event")) {
                if (commands.length == 1) {
                    System.out.println("     OOPS!!! The description of an event cannot be empty.");
                    continue;
                }

                String info = userInput.substring(6).trim();


                String[] parts = info.split(" /from ");
                String description = parts[0];


                String[] timeParts = parts[1].split(" /to ");
                String from = timeParts[0];
                String to = timeParts[1];

                Event temp = new Event(description, from, to);
                tasks.add(temp);
                count++;
                saveTasks(tasks);

                System.out.println("     Got it. I've added this task:");
                System.out.println("       " + tasks.get(count - 1).toString());
                System.out.println("     Now you have " + count + " tasks in the list.");
            }
            else if (commands[0].equalsIgnoreCase("delete")) {
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
            }
            else {
                System.out.println("     OOPS!!! I'm sorry, but I don't know what that means :-(");
            }

            if (!userInput.equalsIgnoreCase("bye")) {
                System.out.println(divider);
            }
        }
        scanner.close();
    }
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
                    t = new Deadline(description, parts[3]);
                } else if (type.equals("E")) {
                    t = new Event(description, parts[3], parts[4]);
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




class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsUndone() {
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "[X]" : "[ ]");
    }

    public String getName() {
        return this.description;
    }

    public String toFileFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }
}

class ToDo extends Task {
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

class Event extends Task {
    protected String from;
    protected String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toFileFormat() {
        return "E | " + super.toFileFormat() + " | " + from + " | " + to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}


class Deadline extends Task {
    protected String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}