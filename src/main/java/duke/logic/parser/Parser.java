package duke.logic.parser;

import duke.exception.DukeInvalidCommandException;
import duke.logic.command.DeadlineCommand;
import duke.logic.command.DeleteCommand;
import duke.logic.command.DoneCommand;
import duke.logic.command.EventCommand;
import duke.logic.command.FindCommand;
import duke.logic.command.ListCommand;
import duke.logic.command.ToDoCommand;
import duke.logic.tasks.Deadline;
import duke.logic.tasks.Event;
import duke.logic.tasks.TaskList;
import duke.logic.tasks.ToDo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Input parsing and displaying output to the user.
 */
public class Parser {
    private static final String UNKNOWN_COMMAND_ERR_MSG = "OOPS!!! I'm sorry, but I don't know what that means :-(";
    private static final String INVALID_FIND_KEYWORD_ERR_MSG = "OOPS!!! Type in the keyword you want to search";
    private static final String EMPTY_LIST_ERR_MSG = "OOPS!!! The task list is currently empty.";
    private static final String UNKNOWN_DELETE_INDEX_ERR_MSG = "OOPS!!! Which task do you want to delete?";
    private static final String EMPTY_TODO_DESCRIPTION_ERR_MSG = "OOPS!!! The description of a todo task cannot be empty.";
    private static final String WRONG_TIME_FORMAT_ERR_MSG = "OOPS!!! Wrong time format. Correct format should be yyyy-mm-dd";
    private static final String WRONG_EVENT_FORMAT_ERR_MSG = "OOPS!!! Wrong format. \n" +
            "\t Correct format should be: event <event_description> /at <event_time>";
    private static final String EMPTY_EVENT_DESCRIPTION_ERR_MSG = "OOPS!!! The description of an event cannot be empty.";
    private static final String WRONG_DEADLINE_FORMAT_ERR_MSG = "OOPS!!! Wrong format. \n" +
            "\t Correct format should be: deadline <deadline_description> /by <deadline_time>";
    private static final String EMPTY_DEADLINE_DESCRIPTION_ERR_MSG = "OOPS!!! The description of a deadline cannot be empty.";
    private static final String UNKNOWN_DONE_INDEX_ERR_MSG = "OOPS!!! Which task do you want to mark as done?";
    private static final String INVALID_LIST_COMMAND_ERR_MSG = "OOPS!!! Do you mean 'list' ?";
    private static final String INVALID_NUMBER_ARGUMENT_ERR_MSG = "OOPS!!! The task number you type in is not a number.";
    private static final String OUT_OF_BOUNDS_ERR_MSG = "OOPS!!! The task number should be between 0 and ";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final TaskList taskList;
    
    /**
     * Constructor for the class.
     */
    public Parser(TaskList taskList) {
        this.taskList = taskList;
    }

    private String[] parseInput(String input) {
        assert(input.length() > 0);
        return input.trim().split(" ", 2);
    }

    private int parseTaskIndex(String input) throws DukeInvalidCommandException {
        assert(input.length() > 0);
        int parsedNumber;
        try {
            parsedNumber = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new DukeInvalidCommandException(INVALID_NUMBER_ARGUMENT_ERR_MSG);
        }
        if (parsedNumber < 0 || parsedNumber > taskList.getSize()) {
            throw new DukeInvalidCommandException(OUT_OF_BOUNDS_ERR_MSG + taskList.getSize() + ".");
        }
        return parsedNumber;
    }

    private String[] getTaskArguments(String input, String regex, String errorMsg) 
            throws DukeInvalidCommandException {
        String[] parsedArguments = input.split(regex);
        if (parsedArguments.length != 2) {
            throw new DukeInvalidCommandException(errorMsg);
        }
        return parsedArguments;
    }

    /** Check if the parsed input has enough arguments to create a new task */
    private void checkValidTaskCreation(String[] input, String errorMsg) throws DukeInvalidCommandException {
        if (input.length < 2) {
            throw new DukeInvalidCommandException(errorMsg);
        }
    }
    private String handleList(String[] parsedInput) throws DukeInvalidCommandException {
        assert(parsedInput.length != 0);
        if (parsedInput.length >= 2) {
            throw new DukeInvalidCommandException(INVALID_LIST_COMMAND_ERR_MSG);
        } else {
            return new ListCommand().executeCommand(taskList);
        }
    }

    private String handleDone(String[] parsedInput) throws DukeInvalidCommandException {
        assert(parsedInput.length != 0);
        checkValidTaskCreation(parsedInput, UNKNOWN_DONE_INDEX_ERR_MSG);
        int taskIndex = parseTaskIndex(parsedInput[1]);
        if (taskList.getSize() == 0) {
            throw new DukeInvalidCommandException(EMPTY_LIST_ERR_MSG);
        }
        return new DoneCommand(taskIndex).executeCommand(taskList);
    }

    private String handleDeadline(String[] parsedInput) throws DukeInvalidCommandException {
        assert(parsedInput.length != 0);
        checkValidTaskCreation(parsedInput, EMPTY_DEADLINE_DESCRIPTION_ERR_MSG);
        String[] parsedArguments = getTaskArguments(parsedInput[1], " /by ", WRONG_DEADLINE_FORMAT_ERR_MSG);
        try {
            LocalDate date = LocalDate.parse(parsedArguments[1], DATE_TIME_FORMATTER);
            return new DeadlineCommand(new Deadline(parsedArguments[0], date)).executeCommand(taskList);
        } catch (DateTimeParseException e) {
            throw new DukeInvalidCommandException(WRONG_TIME_FORMAT_ERR_MSG);
        }
    }

    private String handleEvent(String[] parsedInput) throws DukeInvalidCommandException {
        assert(parsedInput.length != 0);
        checkValidTaskCreation(parsedInput, EMPTY_EVENT_DESCRIPTION_ERR_MSG);
        String[] parsedArguments = getTaskArguments(parsedInput[1], " /at ", WRONG_EVENT_FORMAT_ERR_MSG);
        try {
            LocalDate date = LocalDate.parse(parsedArguments[1], DATE_TIME_FORMATTER);
            return new EventCommand(new Event(parsedArguments[0], date)).executeCommand(taskList);
        } catch (DateTimeParseException e) {
            throw new DukeInvalidCommandException(WRONG_TIME_FORMAT_ERR_MSG);
        }
    }

    private String handleTodo(String[] parsedInput) throws DukeInvalidCommandException {
        if (parsedInput.length < 2) {
            throw new DukeInvalidCommandException(EMPTY_TODO_DESCRIPTION_ERR_MSG);
        }
        checkValidTaskCreation(parsedInput, EMPTY_TODO_DESCRIPTION_ERR_MSG);
        return new ToDoCommand(new ToDo(parsedInput[1])).executeCommand(taskList);
    }

    private String handleDelete(String[] parsedInput) throws DukeInvalidCommandException {
        if (parsedInput.length < 2) {
            throw new DukeInvalidCommandException(UNKNOWN_DELETE_INDEX_ERR_MSG);
        }
        int taskIndex = parseTaskIndex(parsedInput[1]);

        if (taskList.getSize() == 0) {
            throw new DukeInvalidCommandException(EMPTY_LIST_ERR_MSG);
        }
        return new DeleteCommand(taskIndex).executeCommand(taskList);
    }

    private String handleFind(String[] parsedInput) throws DukeInvalidCommandException {
        if (parsedInput.length < 2) {
            throw new DukeInvalidCommandException(INVALID_FIND_KEYWORD_ERR_MSG);
        }
        return new FindCommand(parsedInput[1]).executeCommand(taskList);
    }

    public String invokeCommand(String input) throws DukeInvalidCommandException {
        String[] parsedInput = parseInput(input);
        switch (parsedInput[0]) {
        case "list":
            return handleList(parsedInput);
        case "done":
            return handleDone(parsedInput);
        case "deadline":
            return handleDeadline(parsedInput);
        case "event":
            return handleEvent(parsedInput);
        case "todo":
            return handleTodo(parsedInput);
        case "delete":
            return handleDelete(parsedInput);
        case "find":
            return handleFind(parsedInput);
        default:
            throw new DukeInvalidCommandException(UNKNOWN_COMMAND_ERR_MSG);
        }
    }

}