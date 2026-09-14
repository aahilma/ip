package genie.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

/** Deals with loading tasks from and saving tasks to a file. */
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

                Task task = null;
                if (type.equals("T")) {
                    task = new ToDo(description);
                } else if (type.equals("D")) {
                    LocalDateTime by = LocalDateTime.parse(parts[3], Genie.INPUT_FORMAT);
                    task = new Deadline(description, by);
                } else if (type.equals("E")) {
                    LocalDateTime from = LocalDateTime.parse(parts[3], Genie.INPUT_FORMAT);
                    LocalDateTime to = LocalDateTime.parse(parts[4], Genie.INPUT_FORMAT);
                    task = new Event(description, from, to);
                }

                if (task != null) {
                    if (isDone) {
                        task.markAsDone();
                    }
                    tasks.add(task);
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
            for (Task task : tasks) {
                fw.write(task.toFileFormat() + System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("     Error saving tasks: " + e.getMessage());
        }
    }
}
