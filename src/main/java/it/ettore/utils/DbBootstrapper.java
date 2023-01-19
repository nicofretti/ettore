package it.ettore.utils;

import it.ettore.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
        // Create a couple of students
        User student1 = new User("Student", "One", "student.one@ettore.it", "student.one@ettore.it", User.Role.STUDENT);
        User student2 = new User("Student", "Two", "student.two@ettore.it", "student.two@ettore.it", User.Role.STUDENT);
        User student3 = new User("Student", "Three", "student.three@ettore.it", "student.three@ettore.it", User.Role.STUDENT);
        User student4 = new User("Student", "Four", "student.four@ettore.it", "student.four@ettore.it", User.Role.STUDENT);
        repoUser.saveAll(List.of(student1, student2, student3, student4));

        User professor = new User("B", "Professor", "a.professor@ettore.it", "a.professor@ettore.it", User.Role.PROFESSOR);
        repoUser.save(professor);

        // Create a couple of courses
        Course math = new Course("Maths", "Maths course", 2023, Course.Category.Maths, professor);
        Course history = new Course("History", "History course", 2023, Course.Category.History, professor);
        Course art = new Course("Art", "Art course", 2023, Course.Category.Art, professor);
        repoCourse.saveAll(List.of(math, history, art));

        // Link the courses to the professor
        professor.getCoursesTaught().addAll(List.of(math, history, art));
        repoUser.save(professor);

        // Set students that want to join and that have already joined the math course
        math.setStudentsRequesting(List.of(student3));
        math.setStudentsJoined(List.of(student1, student4));
        repoCourse.save(math);

        // Set students that want to join and that have already joined the history course
        history.setStudentsRequesting(List.of(student1, student3));
        history.setStudentsJoined(List.of(student2, student4));
        repoCourse.save(history);

        // Set students that want to join and that have already joined the art course
        art.setStudentsRequesting(List.of(student2));
        art.setStudentsJoined(List.of(student1, student4));
        repoCourse.save(art);

        // Add some lessons about maths
        Lesson lesson1 = new Lesson("Derivatives", "Some nice description on Derivation", "Derivatives lesson content and stuff `super` " +
                "\n# Intro\nThe derivative of a `function` of a real **variable** measures the sensitivity to change of the function value (_output value_) with respect to its argument (_input value_).\n"
                + "```java\nSystem.out.println(\"f'(x)\")\n```", math);

        Lesson lesson2 = new Lesson("Integrals", "Some nice description on Integration", "Integrals `lesson` content and stuff", math);
        repoLesson.saveAll(List.of(lesson1, lesson2));

        // And link them with the maths course
        math.getLessons().addAll(List.of(lesson1, lesson2));
        repoCourse.save(math);
    }
}
