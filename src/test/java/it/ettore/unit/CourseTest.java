package it.ettore.unit;

import it.ettore.model.Course;
import it.ettore.model.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class CourseTest {
    @Test
    public void testCreateCourse() {
        // Test that no exception is raised
        User professor = new User("Alessandro", "Barbero", "abarbero@unibo.it", "paleolitico", User.Role.PROFESSOR);
        new Course("Medioevo", "Castelli e cavalieri go boom", 2023, Course.Category.History, professor);
    }

    @Test
    public void testFormatting() {
        User professor = new User("Alessandro", "Barbero", "abarbero@unibo.it", "paleolitico", User.Role.PROFESSOR);
        Course course = new Course("Medioevo", "Castelli e cavalieri go boom", 2023, Course.Category.History, professor);
        course.setId(1);
        assertEquals(course.toString(), "Course{id=1,name=Medioevo}");
    }

    @Test
    public void testFormattingPeriod() {
        User professor = new User("Alessandro", "Barbero", "abarbero@unibo.it", "paleolitico", User.Role.PROFESSOR);
        Course course = new Course("Medioevo", "Castelli e cavalieri go boom", 2023, Course.Category.History, professor);
        assertEquals(course.formatPeriod(), "(2023/2024)");
    }
}
