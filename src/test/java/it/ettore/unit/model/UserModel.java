package it.ettore.unit.model;

import it.ettore.model.Course;
import it.ettore.model.User;

import org.junit.Test;


import static org.junit.Assert.*;

public class UserModel {
    public static User dummyProfessor() {
        return new User("A", "Professor", "a.professor@ettore.it", "SomeVerySecurePassword", User.Role.PROFESSOR);
    }

    public static User dummyStudent() {
        return new User("A", "Student", "a.student@ettore.it", "SomeVerySecurePassword", User.Role.STUDENT);
    }

    @Test
    public void createProfessor() {
        // Test that no exception is raised when creating a professor with a long-enough password and a valid email
        dummyProfessor();
    }

    @Test
    public void createStudent() {
        // Test that no exception is raised when creating a student with a long-enough password and a valid email
        dummyStudent();
    }

    @Test
    public void getFirstName() {
        assertEquals("A", dummyProfessor().getFirstName());
    }

    @Test
    public void getLastName() {
        assertEquals("Professor", dummyProfessor().getLastName());
    }

    @Test
    public void getEmail() {
        assertEquals("a.professor@ettore.it", dummyProfessor().getEmail());
    }

    @Test
    public void getPswHash() {
        // The same password should get hashed to the same digest
        assertEquals(User.hashPsw("SomeVerySecurePassword"), dummyProfessor().getPswHash());
    }

    @Test
    public void getRole() {
        assertEquals(User.Role.PROFESSOR, dummyProfessor().getRole());
        assertEquals(User.Role.STUDENT, dummyStudent().getRole());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setEmailInvalidFormat() {
        User user = dummyProfessor();
        user.setEmail("a.professor@");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setShortPassword() {
        User user = dummyProfessor();
        user.setPassword("short");
    }

    @Test
    public void displaying() {
        User user = dummyProfessor();
        user.setId(1);
        assertEquals(user.toString(), "User{id=1,email=a.professor@ettore.it,role=PROFESSOR}");
    }

    @Test
    public void addCourse() {
        User professor = dummyProfessor();
        // Test that no exception is raised when adding a course to the ones taught by a professor
        professor.getCoursesTaught().add(new Course("Computer Architecture", "Bits are bits", 2023, Course.Category.Science, professor));
    }

    @Test
    public void removeCourse() {
        User professor = dummyProfessor();
        // Test that no exception is raised when adding a course to the ones taught by a professor
        professor.getCoursesTaught().add(new Course("Computer Architecture", "Bits are bits", 2023, Course.Category.Science, professor));
        professor.getCoursesTaught().remove(0);
    }
}
