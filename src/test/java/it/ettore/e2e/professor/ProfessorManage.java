package it.ettore.e2e.professor;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage.CourseComponent;
import it.ettore.e2e.po.professor.ProfessorManagePage;
import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import it.ettore.unit.model.CourseModel;
import it.ettore.unit.model.UserModel;
import it.ettore.utils.Breadcrumb;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class ProfessorManage extends E2EBaseTest {
    @Autowired
    protected CourseRepository repoCourse;

    @Test
    public void approveJoinRequest() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);

        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);

        User student = new User("Some", "Student", "some.student@ettore.it", "ChocolatePizza", User.Role.STUDENT);
        course.requestJoin(student);

        repoCourse.save(course);

        // Login with the professor's account
        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        ProfessorCoursesPage coursesPage = loginPage.loginAsProfessor();
        ProfessorCoursePage coursePage = coursesPage.getCourses().get(0).goTo();
        ProfessorManagePage managePage = coursePage.gotoManage();

        // Should be in the manage page for the course
        assertEquals(String.format("/professor/courses/%d/manage", course.getId()), currentPath());

        // Should have one student asking to join
        assertEquals(1, managePage.getStudentsRequesting().size());
        assertEquals("Some Student", managePage.getStudentsRequesting().get(0).getFullName());
        // Should have no students already in the course
        assertEquals(0, managePage.getStudentsJoined().size());

        // Approve join request
        managePage = managePage.getStudentsRequesting().get(0).approve();

        // Should still be in the manage page for the course
        assertEquals(String.format("/professor/courses/%d/manage", course.getId()), currentPath());

        // Should now have one student subscribed and none waiting to subscribe
        assertEquals(0, managePage.getStudentsRequesting().size());
        assertEquals(1, managePage.getStudentsJoined().size());
        assertEquals("Some Student", managePage.getStudentsJoined().get(0).getFullName());
    }

    @Test
    public void rejectJoinRequest() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);

        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);

        User student = new User("Some", "Student", "some.student@ettore.it", "ChocolatePizza", User.Role.STUDENT);
        course.requestJoin(student);

        repoCourse.save(course);

        // Login with the professor's account
        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        ProfessorCoursesPage coursesPage = loginPage.loginAsProfessor();
        ProfessorCoursePage coursePage = coursesPage.getCourses().get(0).goTo();
        ProfessorManagePage managePage = coursePage.gotoManage();

        // Should be in the manage page for the course
        assertEquals(String.format("/professor/courses/%d/manage", course.getId()), currentPath());

        // Should have one student asking to join
        assertEquals(1, managePage.getStudentsRequesting().size());
        assertEquals("Some Student", managePage.getStudentsRequesting().get(0).getFullName());
        // Should have no students already in the course
        assertEquals(0, managePage.getStudentsJoined().size());

        // Reject join request
        managePage = managePage.getStudentsRequesting().get(0).reject();

        // Should still be in the manage page for the course
        assertEquals(String.format("/professor/courses/%d/manage", course.getId()), currentPath());

        // Should now have no student subscribed and none waiting to subscribe
        assertEquals(0, managePage.getStudentsRequesting().size());
        assertEquals(0, managePage.getStudentsJoined().size());
    }

    @Test
    public void removeStudent() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);

        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);

        User student = new User("Some", "Student", "some.student@ettore.it", "ChocolatePizza", User.Role.STUDENT);
        course.requestJoin(student);
        // Immediately accept join request so we can remove him
        course.acceptStudent(student);

        repoCourse.save(course);

        // Login with the professor's account
        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        ProfessorCoursesPage coursesPage = loginPage.loginAsProfessor();
        ProfessorCoursePage coursePage = coursesPage.getCourses().get(0).goTo();
        ProfessorManagePage managePage = coursePage.gotoManage();

        // Should be in the manage page for the course
        assertEquals(String.format("/professor/courses/%d/manage", course.getId()), currentPath());

        // Should have one student already subscribed and none waiting to join
        assertEquals(0, managePage.getStudentsRequesting().size());
        assertEquals(1, managePage.getStudentsJoined().size());
        assertEquals("Some Student", managePage.getStudentsJoined().get(0).getFullName());

        // Reject join request
        managePage = managePage.getStudentsJoined().get(0).remove();

        // Should still be in the manage page for the course
        assertEquals(String.format("/professor/courses/%d/manage", course.getId()), currentPath());

        // Should now have no student subscribed and none waiting to subscribe
        assertEquals(0, managePage.getStudentsRequesting().size());
        assertEquals(0, managePage.getStudentsJoined().size());
    }
}
