package it.ettore.unit.model;

import it.ettore.model.Course;
import it.ettore.model.Lesson;
import it.ettore.model.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class LessonModel {
    public static Lesson dummyLesson(Course course) {
        return new Lesson("Castles", "The stuff made with rocks", "Castles were built in the medieval period", course);
    }

    @Test
    public void createLesson() {
        // Test that no exception is raised when creating a lesson
        User professor = UserModel.dummyProfessor();
        Course course = CourseModel.dummyCourse(professor);
        dummyLesson(course);
    }

    @Test
    public void getTitle() {
        assertEquals("Castles", dummyLesson(CourseModel.dummyCourse(UserModel.dummyProfessor())).getTitle());
    }

    @Test
    public void getDescription() {
        assertEquals("The stuff made with rocks", dummyLesson(CourseModel.dummyCourse(UserModel.dummyProfessor())).getDescription());
    }

    @Test
    public void getContent() {
        assertEquals("Castles were built in the medieval period", dummyLesson(CourseModel.dummyCourse(UserModel.dummyProfessor())).getContent());
    }

    @Test
    public void getCourse() {
        Course course = CourseModel.dummyCourse(UserModel.dummyProfessor());
        assertEquals(course, dummyLesson(course).getCourse());
    }

    @Test
    public void formatting() {
        Lesson lesson = dummyLesson(CourseModel.dummyCourse(UserModel.dummyProfessor()));
        lesson.setId(1);
        assertEquals("Lesson{id=1,title=Castles}", lesson.toString());
    }
}
