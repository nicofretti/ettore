package it.ettore.e2e.professor.lessons;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage.CourseComponent;
import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import it.ettore.utils.Breadcrumb;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProfessorLessons extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;

    void clearDb() {
        repoCourse.deleteAll();
        repoUser.deleteAll();
    }
}
