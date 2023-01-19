package it.ettore.unit.model;

import it.ettore.model.Course;
import it.ettore.model.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class CourseModel {
    public static Course dummyCourse(User professor) {
        return new Course("Middle ages", "Castles and knights go boom", 2023, Course.Category.History, professor);
    }

    @Test
    public void getName() {
        assertEquals("Middle ages", dummyCourse(UserModel.dummyProfessor()).getName());
    }

    @Test
    public void getDescription() {
        assertEquals("Castles and knights go boom", dummyCourse(UserModel.dummyProfessor()).getDescription());
    }

    @Test
    public void getIcon() {
        assertEquals("fa fa-book", dummyCourse(UserModel.dummyProfessor()).getIcon());
    }

    @Test
    public void getStartingYear() {
        assertEquals(2023, dummyCourse(UserModel.dummyProfessor()).getStartingYear());
    }

    @Test
    public void getCategory() {
        assertEquals(Course.Category.History, dummyCourse(UserModel.dummyProfessor()).getCategory());
    }

    @Test
    public void getProfessor() {
        User professor = UserModel.dummyProfessor();
        assertEquals(professor, dummyCourse(professor).getProfessor());
    }

    @Test
    public void createCourse() {
        // Test that no exception is raised when creating a course
        User professor = UserModel.dummyProfessor();
        dummyCourse(professor);
    }

    @Test
    public void formatting() {
        User professor = UserModel.dummyProfessor();
        Course course = dummyCourse(professor);
        course.setId(1);
        assertEquals("Course{id=1,name=Middle ages}",course.toString());
    }

    @Test
    public void period() {
        User professor = UserModel.dummyProfessor();
        Course course = dummyCourse(professor);
        assertEquals("(2023/2024)",course.formatPeriod());
    }

    @Test
    public void fromString() {
        User professor = UserModel.dummyProfessor();
        Course course = dummyCourse(professor);
        assertEquals(Course.Category.History, Course.Category.fromString("History"));
    }

    @Test
    public void toStringTest() {
        User professor = UserModel.dummyProfessor();
        Course course = dummyCourse(professor);
        assertEquals("History", course.getCategory().toString());
    }

    @Test
    public void getAllIcons() {
        User professor = new User();
        // List of courses, each of its own category
        List<Course> courses = List.of(
                new Course("", "", 2021, Course.Category.Maths, professor),
                new Course("", "", 2021, Course.Category.Science, professor),
                new Course("", "", 2021, Course.Category.History, professor),
                new Course("", "", 2021, Course.Category.Geography, professor),
                new Course("", "", 2021, Course.Category.Art, professor),
                new Course("", "", 2021, Course.Category.Music, professor),
                new Course("", "", 2021, Course.Category.Languages, professor)
        );
        Set<String> icons = courses.stream().map(Course::getIcon).collect(Collectors.toSet());
        // Each course should have its own different icon, therefore the size of the list should match the size of
        // the set (which removes duplicates)
        assertEquals(courses.size(), icons.size());
        // Check that all icons follow the expected format
        icons.stream().allMatch(icon -> icon.startsWith("fa fa-"));
    }

    /**
     * Checks that toString and fromString work for User.Category. Check performed by formatting each possible category
     * to a string and then asserting that the parsed category comes back the same as it was before
     */
    @Test
    public void parseAndFormatCategory() {
        User professor = new User();
        // List of courses, each of its own category
        List<Course> courses = List.of(
                new Course("", "", 2021, Course.Category.Maths, professor),
                new Course("", "", 2021, Course.Category.Science, professor),
                new Course("", "", 2021, Course.Category.History, professor),
                new Course("", "", 2021, Course.Category.Geography, professor),
                new Course("", "", 2021, Course.Category.Art, professor),
                new Course("", "", 2021, Course.Category.Music, professor),
                new Course("", "", 2021, Course.Category.Languages, professor)
        );
        courses.forEach(course -> assertEquals(course.getCategory(), Course.Category.fromString(course.getCategory().toString())));
    }

    /**
     * Checks that a students can be added to the pending join requests
     */
    @Test
    public void requestJoin() {
        Course course = dummyCourse(UserModel.dummyProfessor());
        User student = UserModel.dummyStudent();
        course.requestJoin(student);
        assertEquals(1, course.getStudentsRequesting().size());
        assertEquals(student.getId(), course.getStudentsRequesting().get(0).getId());
    }

    /**
     * Checks that a student can be accepted to join a course
     */
    @Test
    public void acceptJoinRequest() {
        Course course = dummyCourse(UserModel.dummyProfessor());
        User student = UserModel.dummyStudent();
        course.requestJoin(student);
        course.acceptStudent(student);
        // No more pending requests
        assertEquals(0, course.getStudentsRequesting().size());
        // One student enrolled
        assertEquals(1, course.getStudentsJoined().size());
        assertEquals(student.getId(), course.getStudentsJoined().get(0).getId());
    }

    /**
     * Checks that a student cannot be accepted to join a course when he/she is already enrolled
     */
    @Test(expected = IllegalStateException.class)
    public void cannotAcceptEnrolled() {
        Course course = dummyCourse(UserModel.dummyProfessor());
        User student = UserModel.dummyStudent();
        course.requestJoin(student);
        course.acceptStudent(student);
        // Cannot accept student already enrolled
        course.acceptStudent(student);
    }

    /**
     * Checks that a student cannot be denied access to join a course when he/she has no pending join request
     */
    @Test(expected = IllegalStateException.class)
    public void cannotDenyEnrolled() {
        Course course = dummyCourse(UserModel.dummyProfessor());
        User student = UserModel.dummyStudent();
        course.requestJoin(student);
        course.acceptStudent(student);
        // Cannot deny student already enrolled
        course.rejectStudent(student);
    }

    /**
     * Checks that a student can be denied to join a course
     */
    @Test
    public void denyJoinRequest() {
        Course course = dummyCourse(UserModel.dummyProfessor());
        User student = UserModel.dummyStudent();
        course.requestJoin(student);
        course.rejectStudent(student);
        // No more pending requests
        assertEquals(0, course.getStudentsRequesting().size());
        // No students enrolled
        assertEquals(0, course.getStudentsJoined().size());
    }

    /**
     * Checks that a student can be removed from a course
     */
    @Test
    public void removeStudent() {
        Course course = dummyCourse(UserModel.dummyProfessor());
        User student = UserModel.dummyStudent();
        course.requestJoin(student);
        course.acceptStudent(student);

        course.removeStudent(student);

        // No more pending requests
        assertEquals(0, course.getStudentsRequesting().size());
        // No students enrolled
        assertEquals(0, course.getStudentsJoined().size());
    }
}
