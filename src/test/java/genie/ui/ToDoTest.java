package genie.ui;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
