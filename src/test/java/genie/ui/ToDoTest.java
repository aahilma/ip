package genie.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

public class ToDoTest {
    @Test
    public void dummyTest() {
        // A simple test to verify JUnit is running correctly
        assertEquals(2, 1 + 1);
    }

    @Test
    public void toString_completedDeadline_validDescription() {
        // Create a new Deadline
        LocalDateTime by = LocalDateTime.parse("21/7/2026 1800", DateTimeFormatter.ofPattern("d/M/yyyy HHmm"));
        Deadline deadline = new Deadline("do dishes", by);
        deadline.markAsDone();


        assertEquals("[D][X] do dishes (by: Jul 21 2026, 6:00 PM)", deadline.toString());

    }

    @Test
    public void toString_createToDo_validDescription() {
        // Create a new ToDo object
        ToDo todo = new ToDo("read book");


        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_completedToDo_validDescription() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toString_undoneToDo_validDescription() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();
        todo.markAsUndone();

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileFormat_incompleteToDo_validDescription() {
        ToDo todo = new ToDo("read book");

        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void toFileFormat_completedToDo_validDescription() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();

        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void getName_createdToDo_returnsDescription() {
        ToDo todo = new ToDo("read book");

        assertEquals("read book", todo.getName());
    }

    @Test
    public void setDescription_createdToDo_updatesDescription() {
        ToDo todo = new ToDo("read book");
        todo.setDescription("write notes");

        assertEquals("[T][ ] write notes", todo.toString());
    }
}
