package it.ettore.utils;

import it.ettore.model.User;
import it.ettore.model.UserRepository;
import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DbBootstrapper {
    @Autowired
    UserRepository repoUser;

    @Autowired
    CourseRepository repoCourse;

    @PostConstruct
    public void bootstrap() {
        repoUser.save(new User("A.", "Student", "a.student@ettore.it", "a.student@ettore.it", User.Role.STUDENT));
        User professor = new User("B.", "Professor", "a.professor@ettore.it", "a.professor@ettore.it", User.Role.PROFESSOR);
        // Add math and history course to the professor
        repoCourse.saveAll(List.of(
                new Course("Maths", "Maths course", 2023, Course.Category.Maths, professor),
                new Course("History", "History course", 2023, Course.Category.History, professor),
                new Course("Art", "Art course", 2023, Course.Category.Art, professor)
            )
        );
    }

}
