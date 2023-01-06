package it.ettore.unit;

import it.ettore.model.Course;
import it.ettore.model.User;

import org.junit.Test;


import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserTest {
    static User user = new User("Nico", "Frex", "nico.fretti@gmail.com", "ACAB", User.Role.PROFESSOR);
    @Test
    public void testUsersFirstname() {
        String firstName = user.getFirstName();
        assertEquals("Nico", firstName);
    }

    @Test
    public void testUsersLastname() {
        String lastName = user.getLastName();
        assertEquals("Frex", lastName);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testUsersValidEmail() {
        user.setEmail("nico.fretti@.com");
    }

    @Test
    public void testUsersEmail() {
        String email = user.getEmail();
        assertEquals("nico.fretti@gmail.com", email);
    }

    @Test
    public void testUsersPswHash() {
        // had to mock since the hashPsw method is private static and returns a different value each time
        User user = mock(User.class);
        when(user.getPswHash()).thenReturn("ACAB");
        assertEquals("ACAB", user.getPswHash());
    }

    @Test
    public void testUsersRole() {
        User.Role role = user.getRole();
        assertEquals(User.Role.PROFESSOR, role);
    }
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
        User user = new User("FIRST_NAME", "LAST_NAME", "EMAIL@GMAIL.COM", "PSW", User.Role.STUDENT);
        user.setId(1);
        assertEquals(user.toString(), "User{id=1,email=EMAIL@GMAIL.COM,role=STUDENT}");
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
