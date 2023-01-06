package it.ettore.unit;

import it.ettore.model.Course;
import it.ettore.model.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void testCreateProfessor() {
        // Test that no exception is raised
        new User("Giorgio", "Poi", "giorgio.poi@univr.it", "sferaebbasta", User.Role.PROFESSOR);
    }

    @Test
    public void testCreateStudent() {
        // Test that no exception is raised
        new User("Massimo", "Mina", "massimo.mina@studenti.univr.it", "nutella", User.Role.STUDENT);
    }

    @Test
    public void testFormatting() {
        User user = new User("FIRST_NAME", "LAST_NAME", "EMAIL", "PSW", User.Role.STUDENT);
        user.setId(1);
        assertEquals(user.toString(), "User{id=1,email=EMAIL,role=STUDENT}");
    }

    @Test
    public void testAddRemoveCourse() {
        // Test that no exception is raised
        User user = new User("Giorgio", "Poi", "giorgio.poi@univr.it", "sferaebbasta", User.Role.PROFESSOR);
        user.getCoursesTaught().add(new Course("COURSE1", "DESCRIPTION", 2023, Course.Category.Maths, user));
        user.getCoursesTaught().add(new Course("COURSE2", "DESCRIPTION", 2022, Course.Category.Maths, user));
        user.getCoursesTaught().add(new Course("COURSE3", "DESCRIPTION", 2023, Course.Category.Maths, user));
        user.getCoursesTaught().add(new Course("COURSE4", "DESCRIPTION", 2022, Course.Category.Maths, user));
        user.getCoursesTaught().removeIf(course -> course.getStartingYear() < 2023);
        assertEquals(user.getCoursesTaught().size(), 2);
    }
}
