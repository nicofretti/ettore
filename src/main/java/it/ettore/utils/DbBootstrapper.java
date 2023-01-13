package it.ettore.utils;

import it.ettore.model.User;
import it.ettore.model.UserRepository;
import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
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

    @PostConstruct
    public void bootstrap() {
        User student1 = new User("Student", "One", "student.one@ettore.it", "student.one@ettore.it", User.Role.STUDENT);
        User student2 = new User("Student", "Two", "student.two@ettore.it", "student.two@ettore.it", User.Role.STUDENT);
        User student3 = new User("Student", "Three", "student.three@ettore.it", "student.three@ettore.it", User.Role.STUDENT);
        User student4 = new User("Student", "Four", "student.four@ettore.it", "student.four@ettore.it", User.Role.STUDENT);

        User professor = new User("B", "Professor", "a.professor@ettore.it", "a.professor@ettore.it", User.Role.PROFESSOR);

        List<Course> courses = List.of(
                new Course("Maths", "Maths course", 2023, Course.Category.Maths, professor),
                new Course("History", "History course", 2023, Course.Category.History, professor),
                new Course("Art", "Art course", 2023, Course.Category.Art, professor)
        );

        courses.get(0).setStudentsRequesting(List.of(student2));
        courses.get(0).setStudentsJoined(List.of(student1, student3, student4));

        courses.get(1).setStudentsRequesting(List.of(student1, student3));
        courses.get(1).setStudentsJoined(List.of(student2, student4));

        courses.get(2).setStudentsJoined(List.of(student1, student2, student3));

        repoCourse.saveAll(courses);
    }

}
