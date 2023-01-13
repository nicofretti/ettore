package it.ettore.unit;

import it.ettore.model.Course;
import it.ettore.model.User;
import org.junit.Test;

import static org.junit.Assert.*;

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
        assertEquals("fa fa-flask", dummyCourse(UserModel.dummyProfessor()).getIcon());
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
        assertEquals(course.toString(), "Course{id=1,name=Middle ages}");
    }

    @Test
    public void period() {
        User professor = UserModel.dummyProfessor();
        Course course = dummyCourse(professor);
        assertEquals(course.formatPeriod(), "(2023/2024)");
    }
}
