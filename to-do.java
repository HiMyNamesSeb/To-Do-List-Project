import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.*;

/**
 * Task — represents a single to-do item.
 */
class Task {
    private String description;
    private boolean completed;

    public Task(String description) {
        this.description = description;
        this.completed = false;
    }

    // Used when loading tasks from file
    public Task(String description, boolean completed) {
        this.description = description;
        this.completed = completed;
    }

    public String getDescription() { return description; }
    public boolean isCompleted()   { return completed; }
    public void complete()         { this.completed = true; }
    public void uncomplete()       { this.completed = false; }

    @Override
    public String toString() {
        String status = completed ? "[✓]" : "[ ]";
        return status + " " + description;
    }

    // Format for saving to file: "description|true" or "description|false"
    public String toFileFormat() {
        return description + "|" + completed;
    }
}

/**
 * TaskManager — handles the list of tasks and file persistence.
 */
class TaskManager {
    private List<Task> tasks;
    private static final String FILE_NAME = "tasks.txt";

    public TaskManager() {
        tasks = new ArrayList<>();
        loadFromFile();
    }

    public void addTask(String description) {
        if (description.isBlank()) {
            System.out.println("  ✗ Task description cannot be empty.");
            return;
        }
        tasks.add(new Task(description.trim()));
        saveToFile();
        System.out.println("  ✓ Task added: \"" + description.trim() + "\"");
    }

    public void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("  No tasks yet. Add one!");
            return;
        }
        System.out.println();
        for (int i = 0; i < tasks.size(); i++) {
            System.out.printf("  %2d. %s%n", i + 1, tasks.get(i));
        }
        System.out.println();
    }

    public void completeTask(int index) {
        if (!isValidIndex(index)) return;
        Task task = tasks.get(index - 1);
        if (task.isCompleted()) {
            System.out.println("  Task already completed.");
        } else {
            task.complete();
            saveToFile();
            System.out.println("  ✓ Marked complete: \"" + task.getDescription() + "\"");
        }
    }

    public void uncompleteTask(int index) {
        if (!isValidIndex(index)) return;
        Task task = tasks.get(index - 1);
        task.uncomplete();
        saveToFile();
        System.out.println("  ↩ Marked incomplete: \"" + task.getDescription() + "\"");
    }

    public void deleteTask(int index) {
        if (!isValidIndex(index)) return;
        Task removed = tasks.remove(index - 1);
        saveToFile();
        System.out.println("  ✗ Deleted: \"" + removed.getDescription() + "\"");
    }

    public void clearCompleted() {
        long before = tasks.size();
        tasks.removeIf(Task::isCompleted);
        long removed = before - tasks.size();
        saveToFile();
        System.out.println("  Removed " + removed + " completed task(s).");
    }

    // ── File I/O ──

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Task task : tasks) {
                writer.println(task.toFileFormat());
            }
        } catch (IOException e) {
            System.out.println("  Warning: Could not save tasks. " + e.getMessage());
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    String desc = parts[0];
                    boolean done = Boolean.parseBoolean(parts[1]);
                    tasks.add(new Task(desc, done));
                }
            }
        } catch (IOException e) {
            System.out.println("  Warning: Could not load saved tasks. " + e.getMessage());
        }
    }

    private boolean isValidIndex(int index) {
        if (tasks.isEmpty()) {
            System.out.println("  No tasks to act on.");
            return false;
        }
        if (index < 1 || index > tasks.size()) {
            System.out.println("  Invalid task number. Enter a number between 1 and " + tasks.size() + ".");
            return false;
        }
        return true;
    }
}

/**
 * TodoApp — entry point and menu loop.
 */
public class TodoApp {

    private static final String DIVIDER = "  " + "─".repeat(40);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager manager = new TaskManager();

        printBanner();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("  > ");
            String input = scanner.nextLine().trim();

            System.out.println(DIVIDER);

            switch (input) {
                case "1" -> {
                    System.out.print("  Enter task description: ");
                    String desc = scanner.nextLine();
                    manager.addTask(desc);
                }
                case "2" -> manager.listTasks();
                case "3" -> {
                    manager.listTasks();
                    System.out.print("  Enter task number to complete: ");
                    manager.completeTask(readInt(scanner));
                }
                case "4" -> {
                    manager.listTasks();
                    System.out.print("  Enter task number to mark incomplete: ");
                    manager.uncompleteTask(readInt(scanner));
                }
                case "5" -> {
                    manager.listTasks();
                    System.out.print("  Enter task number to delete: ");
                    manager.deleteTask(readInt(scanner));
                }
                case "6" -> manager.clearCompleted();
                case "7" -> {
                    System.out.println("  Goodbye! Tasks saved.");
                    running = false;
                }
                default -> System.out.println("  Unknown option. Enter 1–7.");
            }

            System.out.println(DIVIDER);
        }

        scanner.close();
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════╗");
        System.out.println("  ║          JAVA TO-DO LIST  v1.0           ║");
        System.out.println("  ║     Tasks are saved to tasks.txt         ║");
        System.out.println("  ╚══════════════════════════════════════════╝");
        System.out.println();
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("  1. Add task");
        System.out.println("  2. View all tasks");
        System.out.println("  3. Mark task complete");
        System.out.println("  4. Mark task incomplete");
        System.out.println("  5. Delete task");
        System.out.println("  6. Clear all completed");
        System.out.println("  7. Quit");
        System.out.println();
    }

    private static int readInt(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; // invalid, TaskManager handles it
        }
    }
}
