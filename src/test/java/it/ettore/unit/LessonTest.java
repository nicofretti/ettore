package it.ettore.unit;

import it.ettore.model.Course;
import it.ettore.model.Lesson;
import it.ettore.model.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class LessonTest {

    @Test
    public void testCreateLesson() {
        // Test that no exception is raised
        User professor = new User("Alessandro", "Barbero", "abarbero@unibo.it", "paleolitico", User.Role.PROFESSOR);
        Course course = new Course("Medieval Period", "Castles and knights", 2023, Course.Category.History, professor);
        new Lesson("Medieval Period", "Init of the history", "Long time ago there was a castle", course);
    }

    @Test
    public void testFormatting(){
        User professor = new User("Alessandro", "Barbero", "abarbero@unibo.it", "paleolitico", User.Role.PROFESSOR);
        Course course = new Course("Medieval Period", "Castles and knights", 2023, Course.Category.History, professor);
        Lesson lesson = new Lesson("Medieval part I", "Init of the history", "Long time ago there was a castle", course);
        lesson.setId(1);
        assertEquals(lesson.toString(), "Lesson{id=1,title=Medieval part I}");
    }
}
