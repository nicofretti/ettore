package it.ettore.unit;

import it.ettore.model.Course;
import it.ettore.model.Lesson;
import it.ettore.model.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class LessonTest {

    static User professor = new User("Alessandro", "Barbero", "abarbero@unibo.it", "paleolitico", User.Role.PROFESSOR);
    static Course course = new Course("Medieval Period", "Castles and knights", 2023, Course.Category.History, professor);

    static Lesson lesson;
    static {
        lesson = new Lesson("Medieval part I", "Init of the history", "Castles were built in the medieval period", course);
        // Used later
        lesson.setId(1);
    }
    @Test
    public void testLessonsCreate() {
        // Test that no exception is raised
        new Lesson("Medieval part I", "Init of the history", "Castles were built in the medieval period", course);
    }

    @Test
    public void testLessonsId() {
        assertEquals(1, lesson.getId());
    }

    @Test
    public void testLessonsTitle() {
        assertEquals("Medieval part I", lesson.getTitle());
    }

    @Test
    public void testLessonsDescription() {
        assertEquals("Init of the history", lesson.getDescription());
    }

    @Test
    public void testLessonsContent() {
        assertEquals("Castles were built in the medieval period", lesson.getContent());
    }

    @Test
    public void testLessonsCourse() {
        assertEquals(course, lesson.getCourse());
    }

    @Test
    public void testLessonsFormatting(){
        assertEquals(lesson.toString(), "Lesson{id=1,title=Medieval part I}");
    }
}
