package it.ettore.utils;

import it.ettore.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * Initializes an empty database with some data. This is because we're currently using an in-memory database and it
 * would be tedious to start fresh each time the app is rebuilt and restarted.
 */
@Component
public class DbBootstrapper {
    @Autowired
    UserRepository repoUser;
    @Autowired
    CourseRepository repoCourse;

    @Autowired
    LessonRepository repoLesson;

    @PostConstruct
    public void bootstrap() {
        User student1 = new User("Student", "One", "student.one@ettore.it", "student.one@ettore.it", User.Role.STUDENT);
        User student2 = new User("Student", "Two", "student.two@ettore.it", "student.two@ettore.it", User.Role.STUDENT);
        User student3 = new User("Student", "Three", "student.three@ettore.it", "student.three@ettore.it", User.Role.STUDENT);
        User student4 = new User("Student", "Four", "student.four@ettore.it", "student.four@ettore.it", User.Role.STUDENT);

        User professor = new User("B", "Professor", "a.professor@ettore.it", "a.professor@ettore.it", User.Role.PROFESSOR);
        // Add math and history course to the professor
        Course Math = new Course("Maths", "Maths course", 2023, Course.Category.Maths, professor);
        Course History = new Course("History", "History course", 2023, Course.Category.History, professor);
        Course Art = new Course("Art", "Art course", 2023, Course.Category.Art, professor);

        // Add lessons to the courses
        Lesson derivatives = new Lesson("Derivatives", "Some nice description on Derivation","Derivatives lesson content and stuff", Math);
        Lesson integrals = new Lesson("Integrals", "Some nice description on Integration","Integrals lesson content and stuff", Math);
        Lesson history = new Lesson("WW2", "Some nice description on History","History lesson content and stuff", History);
        Lesson art = new Lesson("Art", "Some nice description on Art","Art lesson content and stuff", Art);

        //repoCourse.saveAll(List.of(Math, History, art));
        repoLesson.saveAll(List.of(derivatives, integrals, history, art));
    }

}
