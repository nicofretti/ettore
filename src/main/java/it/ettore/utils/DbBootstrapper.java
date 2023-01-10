package it.ettore.utils;

import it.ettore.model.User;
import it.ettore.model.UserRepository;
import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DbBootstrapper {
    @Autowired
    UserRepository repoUser;

    @Autowired
    CourseRepository repoCourse;

    @PostConstruct
    public void bootstrap() {
        repoUser.save(new User("A", "Student", "a.student@ettore.it", "a.student@ettore.it", User.Role.STUDENT));
        repoCourse.save(new Course("Maths", "Maths course", 2023, Course.Category.Maths, new User("A.", "Professor", "a.professor@ettore.it", "a.professor@ettore.it", User.Role.PROFESSOR)));
    }
}
