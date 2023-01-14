package it.ettore.e2e.professor.courses;

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

import static org.junit.Assert.*;

public class ProfessorCourses extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;

    @Test
    public void breadcrumbs() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        repoUser.save(new User("Some", "Professor", email, password, User.Role.PROFESSOR));

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        ProfessorCoursesPage coursesPage = loginPage.loginAsProfessor();
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses")
        ), coursesPage.headerComponent().getBreadcrumbs());
    }

    @Test
    public void courses() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        ProfessorCoursesPage coursesPage = loginPage.loginAsProfessor();
        List<CourseComponent> courses = coursesPage.getCourses();
        assertEquals(1, courses.size());
        assertEquals("Course name", courses.get(0).getName());
        assertEquals("(2023/2024)", courses.get(0).getPeriod());
        assertEquals("Course description", courses.get(0).getDescription());
        courses.get(0).goTo();

        // Should be in the details page for the course
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());
    }
}
