package it.ettore.unit;

import it.ettore.model.Course;
import it.ettore.model.User;

import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void requestCourseJoin() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();
        course.requestJoin(student);

        // Check that the list of students that want to join the course is exactly a 1-length list containing our dummy
        // student
        assertEquals(List.of(student), course.getStudentsRequesting());
    }

    @Test
    public void approveCourseJoin() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();
        // We need to use this trickery because List.of return an immutable List
        course.setStudentsRequesting(new ArrayList<>(List.of(student)));

        course.acceptStudent(student);

        // There should be no pending join requests anymore, and one student that has joined
        assertEquals(0, course.getStudentsRequesting().size());
        assertEquals(List.of(student), course.getStudentsJoined());
    }

    @Test
    public void rejectCourseJoin() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();
        // We need to use this trickery because List.of return an immutable List
        course.setStudentsRequesting(new ArrayList<>(List.of(student)));

        course.rejectStudent(student);

        // There should be no pending join requests anymore, and no students that have joined
        assertEquals(0, course.getStudentsRequesting().size());
        assertEquals(0, course.getStudentsJoined().size());
    }

    @Test
    public void removeStudentFromCourse() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();
        // We need to use this trickery because List.of return an immutable List
        course.setStudentsJoined(new ArrayList<>(List.of(student)));

        course.removeStudent(student);

        // There should be no students that have joined
        assertEquals(0, course.getStudentsJoined().size());
    }

    @Test(expected = IllegalStateException.class)
    public void requestCourseJoinButAlreadyDid() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();
        // Say the student has already requested to join
        // We need to use this trickery because List.of return an immutable List
        course.setStudentsRequesting(new ArrayList<>(List.of(student)));

        course.requestJoin(student);
    }

    @Test(expected = IllegalStateException.class)
    public void requestCourseJoinButAlreadyIn() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();
        // Say the student has already joined
        // We need to use this trickery because List.of return an immutable List
        course.setStudentsJoined(new ArrayList<>(List.of(student)));

        course.requestJoin(student);
    }

    @Test(expected = IllegalStateException.class)
    public void approveCourseJoinButDidntAsk() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();

        course.acceptStudent(student);
    }

    @Test(expected = IllegalStateException.class)
    public void rejectCourseJoinButDidntAsk() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();

        course.rejectStudent(student);
    }

    @Test(expected = IllegalStateException.class)
    public void approveCourseJoinButAlreadyIn() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();
        // Say the student is already in
        // We need to use this trickery because List.of return an immutable List
        course.setStudentsJoined(new ArrayList<>(List.of(student)));

        course.acceptStudent(student);
    }

    @Test(expected = IllegalStateException.class)
    public void rejectCourseJoinButAlreadyIn() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();
        // Say the student is already in
        // We need to use this trickery because List.of return an immutable List
        course.setStudentsJoined(new ArrayList<>(List.of(student)));

        course.rejectStudent(student);
    }

    @Test(expected = IllegalStateException.class)
    public void removeStudentFromCourseButNotApprovedYet() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();
        // Say the student has requested to join but hasn't been approved yet
        // We need to use this trickery because List.of return an immutable List
        course.setStudentsRequesting(new ArrayList<>(List.of(student)));

        course.removeStudent(student);
    }

    @Test(expected = IllegalStateException.class)
    public void removeStudentFromCourseButNotIn() {
        User professor = dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);

        User student = dummyStudent();
        // Say the student is not in the course and hasn't requested to join

        course.removeStudent(student);
    }
}
