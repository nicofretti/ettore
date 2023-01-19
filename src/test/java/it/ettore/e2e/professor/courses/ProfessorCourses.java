package it.ettore.e2e.professor.courses;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
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
        repoUser.save(professor);

        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);
        // Link the course to the professor
        professor.getCoursesTaught().add(course);
        repoUser.save(professor);

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
        ProfessorCoursePage coursePage = courses.get(0).goTo();

        // Should be in the details page for the course
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());

        // Info in the course details page should match the course that we've created before
        assertEquals("Course name", coursePage.getName());
        assertEquals("(2023/2024)", coursePage.getPeriod());
        assertEquals("Course description", coursePage.getDescription());
    }

    /**
     * Check that the application doesn't allow a professor to navigate or interact with a course that doesn't exist
     */
    @Test
    public void cannotInteractWithNonExistingCourse() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
        repoUser.save(professor);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        loginPage.loginAsProfessor();

        // Try to go to the details of a course that doesn't exist
        driver.get(baseDomain() + "professor/courses/420");
        // Check that we're redirected to the list of courses
        assertEquals("/professor/courses", currentPath());

        // Try to go to the manage page of a course that doesn't exist
        driver.get(baseDomain() + "professor/courses/420/manage");
        // Check that we're redirected to the list of courses
        assertEquals("/professor/courses", currentPath());

        // Try to accept a student to a course that doesn't exist
        driver.get(baseDomain() + "professor/courses/420/accept/1");
        // Check that we're redirected to the list of courses
        assertEquals("/professor/courses", currentPath());

        // Try to reject a student to a course that doesn't exist
        driver.get(baseDomain() + "professor/courses/420/reject/1");
        // Check that we're redirected to the list of courses
        assertEquals("/professor/courses", currentPath());

        // Try to remove a student from a course that doesn't exist
        driver.get(baseDomain() + "professor/courses/420/remove/1");
        // Check that we're redirected to the list of courses
        assertEquals("/professor/courses", currentPath());

        // Try to edit a course that doesn't exist
        driver.get(baseDomain() + "professor/courses/420/edit");
        // Check that we're redirected to the list of courses
        assertEquals("/professor/courses", currentPath());

        // Try to delete a course that doesn't exist
        driver.get(baseDomain() + "professor/courses/420/delete");
        // Check that we're redirected to the list of courses
        assertEquals("/professor/courses", currentPath());
    }

    /**
     * Check that the application doesn't allow a professor to navigate or interact with a course that he/she doesn't
     * teach
     */
    @Test
    public void cannotInteractWithCourseNotTaught() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
        repoUser.save(professor);

        User anotherProfessor = new User("Another", "Professor", "another.professor@ettore.it", "AnotherProfessorPassword", User.Role.PROFESSOR);
        repoUser.save(anotherProfessor);

        Course course = new Course("Course", "Description", 2023, Course.Category.Maths, anotherProfessor);
        repoCourse.save(course);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        loginPage.loginAsProfessor();

        // Try to go to the details of a course that the professor doesn't teach
        driver.get(baseDomain() + "professor/courses/" + course.getId());
        // Check that we're redirected to the list of courses
        assertEquals("/professor/courses", currentPath());

        // Try to go to the manage page of a course that the professor doesn't teach
        driver.get(baseDomain() + "professor/courses/" + course.getId() + "/manage");
        // Check that we're redirected to the list of courses
        assertEquals("/professor/courses", currentPath());

        // Try to edit a course that the professor doesn't teach
        driver.get(baseDomain() + "professor/courses/" + course.getId() + "/edit");
        // Check that we're redirected to the list of courses
        assertEquals("/professor/courses", currentPath());

        // Try to delete a course that the professor doesn't teach
        driver.get(baseDomain() + "professor/courses/" + course.getId() + "/delete");
        // Check that we're redirected to the list of courses
        assertEquals("/professor/courses", currentPath());
    }

    /**
     * Check that the application doesn't allow a professor to accept/deny/remove a student that doesn't exist
     */
    @Test
    public void cannotInteractWithNonExistingStudent() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
        repoUser.save(professor);

        Course course = new Course("Course", "Description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);

        professor.getCoursesTaught().add(course);
        repoUser.save(professor);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        loginPage.loginAsProfessor();

        // Try to accept a student that doesn't exist
        driver.get(baseDomain() + "professor/courses/" + course.getId() + "/accept/420");
        // Check that we're redirected to the details page
        assertEquals("/professor/courses/" + course.getId() + "/manage", currentPath());

        // Try to reject a student that doesn't exist
        driver.get(baseDomain() + "professor/courses/" + course.getId() + "/reject/420");
        // Check that we're redirected to the details page
        assertEquals("/professor/courses/" + course.getId() + "/manage", currentPath());

        // Try to remove a student that doesn't exist
        driver.get(baseDomain() + "professor/courses/" + course.getId() + "/remove/420");
        // Check that we're redirected to the details page
        assertEquals("/professor/courses/" + course.getId() + "/manage", currentPath());
    }
}
