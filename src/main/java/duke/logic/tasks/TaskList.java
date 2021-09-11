package duke.logic.tasks;

import duke.gui.TextUi;
import duke.storage.Storage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The task list to save all the duke.logic.tasks
 */
public class TaskList {
    // Saved duke.logic.tasks
    private final ArrayList<Task> tasks;

    /**
     * Constructor for duke.logic.tasks.TaskList
     */
    public TaskList() {
        ArrayList<Task> temp;
        try {
            temp = Storage.loadTaskListFromHardDisk();
        } catch (IOException e) {
            temp = new ArrayList<>();
            TextUi.display("Can't read the save file.");
        }
        this.tasks = temp;
    }

    /**
     * Add a task to the list
     *
     * @param task The added task.
     * @return The result of the operation.
     */
    public String addTask(Task task) {
        tasks.add(task);

        StringBuilder response = new StringBuilder("Got it. I've added this task:");
        response.append("\t").append("  ").append(task)
                .append("\n\t Now you have ").append(tasks.size());
        if (tasks.size() == 1) {
            response.append(" task in the list.");
        } else {
            response.append(" duke.logic.tasks in the list.");
        }
        Storage.saveTaskListToHardDisk(tasks);
        return response.toString();
    }

    /**
     * Mark a task in the task list as done
     *
     * @param index The user requested index.
     * @return The status of the operation.
     */
    public String markTaskAsDone(int index) {
        assert index <= tasks.size() && index > 0 : "Done index out of bounds";
        StringBuilder response = new StringBuilder();
        if (tasks.get(index - 1).markAsDone()) {
            response.append("Nice! I've marked this task as done:\n");
        } else {
            response.append("This task is already done!");
        }
        response.append("\t" + "  ").append(tasks.get(index - 1).toString());
        Storage.saveTaskListToHardDisk(tasks);
        return response.toString();
    }

    public String deleteTask(int index) {
        assert index <= tasks.size() && index > 0 : "Deleted index out of bounds";
        Task temp = tasks.get(index - 1);
        tasks.remove(index - 1);

        StringBuilder response = new StringBuilder("Noted. I've removed this task:");
        response.append("\t").append("  ").append(temp)
                .append("\n\t Now you have ").append(tasks.size());
        if (tasks.size() == 1) {
            response.append(" task in the list.");
        } else {
            response.append(" duke.logic.tasks in the list.");
        }
        Storage.saveTaskListToHardDisk(tasks);
        return response.toString();
    }

    /**
     * Find a task with a given keyword.
     *
     * @param keyword The given keyword.
     * @return The result of the search.
     */
    public String findTask(String keyword) {
        int count = 0;
        StringBuilder res = new StringBuilder("Here are the matching duke.logic.tasks in your list:\n");
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().contains(keyword)) {
                String temp = "\t" + " " + (i + 1) + "." + tasks.get(i).toString() + "\n";
                res.append(temp);
                count++;
            }
        }
        res.deleteCharAt(res.toString().length() - 1);
        if (count == 0) {
            return "There are no duke.logic.tasks with the given keyword";
        } else {
            return res.toString();
        }
    }

    /**
     * Return the size of the task list
     *
     * @return Size of the task list.
     */
    public int getSize() {
        return tasks.size();
    }

    @Override
    public String toString() {
        if (this.getSize() == 0) {
            return "There is no task in the list";
        } else {
            StringBuilder res = new StringBuilder("Here are the duke.logic.tasks in your list:\n");
            for (int i = 0; i < tasks.size(); i++) {
                String temp = "\t" + " " + (i + 1) + "." + tasks.get(i).toString();
                if (i < tasks.size() - 1) { // remove the last \n char, ugly but get the job done
                    temp += "\n";
                }
                res.append(temp);
            }
            return res.toString();
        }
    }
}