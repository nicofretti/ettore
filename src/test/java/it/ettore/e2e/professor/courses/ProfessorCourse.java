package it.ettore.e2e.professor.courses;

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

public class ProfessorCourse extends E2EBaseTest {
    @Autowired
    protected CourseRepository repoCourse;

    @Autowired
    protected UserRepository repoUser;

    @Test
    public void course() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
        Course course = new Course("Course name", "Course description", 2023, it.ettore.model.Course.Category.Maths, professor);
        professor.getCoursesTaught().add(course);
        repoUser.save(professor);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        loginPage.loginAsProfessor();

        driver.get(baseDomain() + String.format("/professor/courses/%d", course.getId()));

        ProfessorCoursePage coursePage = new ProfessorCoursePage(driver);

        assertEquals("Course name", coursePage.getName());
        assertEquals("Course description", coursePage.getDescription());
        assertEquals("(2023/2024)", coursePage.getPeriod());
    }
}
