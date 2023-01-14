package it.ettore.e2e.professor.lessons;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class ProfessorLesson extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;


}
