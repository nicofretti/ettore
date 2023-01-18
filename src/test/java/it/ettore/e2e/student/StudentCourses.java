package it.ettore.e2e.student;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.ErrorsComponent;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.student.StudentCoursePage;
import it.ettore.e2e.po.student.StudentCoursesPage;
import it.ettore.e2e.po.student.StudentSearchPage;
import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import it.ettore.utils.Breadcrumb;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static it.ettore.TestUtil.assertEmpty;
import static org.junit.Assert.assertEquals;

public class StudentCourses extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;

    /**
     * Checks that the student's homepage displays the correct list of breadcrumbs
     */
    @Test
    public void breadcrumbs() {
        String email = "some.student@ettore.it";
        String password = "SomeSecurePassword";
        repoUser.save(new User("Some", "Professor", email, password, User.Role.STUDENT));

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        StudentCoursesPage coursesPage = loginPage.loginAsStudent();
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/student/courses")
        ), coursesPage.headerComponent().getBreadcrumbs());
    }

    /**
     * Checks that the student can see, in his/her homepage, his/her joined courses
     */
    @Test
    public void canSeeJoinedCourse() {
        User professor = new User("Some", "Professor", "some.professor@ettore.it", "SomeSecurePasswordProfessor", User.Role.PROFESSOR);
        repoUser.save(professor);

        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);

        String email = "some.student@ettore.it";
        String password = "SomeSecurePasswordStudent";
        User student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        course.requestJoin(student);
        course.acceptStudent(student);
        repoCourse.save(course);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        StudentCoursesPage coursesPage = loginPage.loginAsStudent();
        List<StudentCoursesPage.CourseComponent> courses = coursesPage.getCourses();
        assertEquals(1, courses.size());
        assertEquals("Course name", courses.get(0).getName());
        assertEquals("(2023/2024)", courses.get(0).getPeriod());
        assertEquals("Course description", courses.get(0).getDescription());
    }

    /**
     * Checks that the student can't see a course that he/she hasn't even requested to join
     */
    @Test
    public void cannotSeeUnrequestedCourse() {
        User professor = new User("Some", "Professor", "some.professor@ettore.it", "SomeSecurePasswordProfessor", User.Role.PROFESSOR);
        repoUser.save(professor);

        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);

        String email = "some.student@ettore.it";
        String password = "SomeSecurePasswordStudent";
        User student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        StudentCoursesPage coursesPage = loginPage.loginAsStudent();
        List<StudentCoursesPage.CourseComponent> courses = coursesPage.getCourses();
        // The user is fresh and hasn't requested to join any course, so he/she should see none
        assertEmpty(courses);
    }

    /**
     * Checks that the student can't see a course that he/she has requested to join but hasn't been accepted yet
     */
    @Test
    public void cannotSeeUnjoinedCourse() {
        User professor = new User("Some", "Professor", "some.professor@ettore.it", "SomeSecurePasswordProfessor", User.Role.PROFESSOR);
        repoUser.save(professor);

        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);

        String email = "some.student@ettore.it";
        String password = "SomeSecurePasswordStudent";
        User student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        course.requestJoin(student);
        repoCourse.save(course);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        StudentCoursesPage coursesPage = loginPage.loginAsStudent();
        List<StudentCoursesPage.CourseComponent> courses = coursesPage.getCourses();
        // The user is fresh and hasn't requested to join any course, so he/she should see none
        assertEmpty(courses);
    }

    /**
     * Checks that the student can see the details of a course he/she is enrolled in
     */
    @Test
    public void canSeeCourseDetails() {
        User professor = new User("Some", "Professor", "some.professor@ettore.it", "SomeSecurePasswordProfessor", User.Role.PROFESSOR);
        repoUser.save(professor);

        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);

        String email = "some.student@ettore.it";
        String password = "SomeSecurePasswordStudent";
        User student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        course.requestJoin(student);
        course.acceptStudent(student);
        repoCourse.save(course);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        StudentCoursesPage coursesPage = loginPage.loginAsStudent();
        List<StudentCoursesPage.CourseComponent> courses = coursesPage.getCourses();
        StudentCoursePage coursePage = courses.get(0).goTo();

        // Should be in the details page for the course
        assertEquals(String.format("/student/courses/%d", course.getId()), currentPath());

        assertEquals("Course name", coursePage.getName());
        assertEquals("(2023/2024)", coursePage.getPeriod());
        assertEquals("Course description", coursePage.getDescription());
    }

    /**
     * Checks that the student can remove him/herself from a course, and it's not shown anymore in the courses list
     */
    @Test
    public void unjoin() {
        User professor = new User("Some", "Professor", "some.professor@ettore.it", "SomeSecurePasswordProfessor", User.Role.PROFESSOR);
        repoUser.save(professor);

        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);

        String email = "some.student@ettore.it";
        String password = "SomeSecurePasswordStudent";
        User student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        course.requestJoin(student);
        course.acceptStudent(student);
        repoCourse.save(course);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        StudentCoursesPage coursesPage = loginPage.loginAsStudent();
        List<StudentCoursesPage.CourseComponent> courses = coursesPage.getCourses();
        StudentCoursePage coursePage = courses.get(0).goTo();
        coursesPage = coursePage.unjoin();

        // Should be in the student's homepage
        assertEquals(String.format("/student/courses", course.getId()), currentPath());

        assertEmpty(coursesPage.getCourses());
    }

    /**
     * Check that the application doesn't allow a student to navigate or interact with a course that doesn't exist
     */
    @Test
    public void cannotInteractWithNonExistingCourse() {
        String email = "some.student@ettore.it";
        String password = "SomeSecurePassword";
        User student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        loginPage.loginAsStudent();

        // Try to go to the details of a course that doesn't exist
        driver.get(baseDomain() + "student/courses/420");
        // Check that we're redirected to the list of courses
        assertEquals("/student/courses", currentPath());

        // Try to unsubscribe from a course that doesn't exist
        driver.get(baseDomain() + "student/courses/420/unjoin");
        // Check that we're redirected to the list of courses
        assertEquals("/student/courses", currentPath());

        // Try to request to join a course that doesn't exist
        driver.get(baseDomain() + "student/courses/420/request-to-join");
        // Check that we're redirected to the list of courses
        assertEquals("/student/courses", currentPath());
    }

    /**
     * Check that the application doesn't allow a student to navigate or interact with a course that he/she isn't
     * enrolled in
     */
    @Test
    public void cannotInteractWithNonJoinedCourse() {
        String email = "some.student@ettore.it";
        String password = "SomeSecurePassword";
        User student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        User professor = new User("Some", "Professor", "some.professor@ettore.it", "SomeSecurePassword", User.Role.PROFESSOR);
        repoUser.save(professor);
        Course course = new Course("Some course", "Description", 2022, Course.Category.Science, professor);
        repoCourse.save(course);
        professor.getCoursesTaught().add(course);
        repoUser.save(professor);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        loginPage.loginAsStudent();

        // Try to go to the details of a course that the student isn't enrolled in
        driver.get(baseDomain() + "student/courses/" + course.getId());
        // Check that we're redirected to the list of courses
        assertEquals("/student/courses", currentPath());

        // Try to unsubscribe from a course that the student isn't enrolled in
        driver.get(baseDomain() + "student/courses/" + course.getId() + "/unjoin");
        // Check that we're redirected to the list of courses
        assertEquals("/student/courses", currentPath());
    }

    /**
     * Check that the application doesn't allow a student to request to join a course that he/she has already requested
     * to join
     */
    @Test
    public void cannotRequestToJoinCourseAlreadyRequested() {
        String email = "some.student@ettore.it";
        String password = "SomeSecurePassword";
        User student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        User professor = new User("Some", "Professor", "some.professor@ettore.it", "SomeSecurePassword", User.Role.PROFESSOR);
        repoUser.save(professor);
        Course course = new Course("Some course", "Description", 2022, Course.Category.Science, professor);
        course.requestJoin(student);
        repoCourse.save(course);
        professor.getCoursesTaught().add(course);
        repoUser.save(professor);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        loginPage.loginAsStudent();

        driver.get(baseDomain() + "student/courses/" + course.getId() + "/request-to-join");
        // Check that we're redirected to the list of courses
        assertEquals("/student/courses", currentPath());
    }

    /**
     * Check that the application doesn't allow a student to request to join a course that he/she is already enrolled in
     */
    @Test
    public void cannotRequestToJoinCourseAlreadyJoined() {
        String email = "some.student@ettore.it";
        String password = "SomeSecurePassword";
        User student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        User professor = new User("Some", "Professor", "some.professor@ettore.it", "SomeSecurePassword", User.Role.PROFESSOR);
        repoUser.save(professor);
        Course course = new Course("Some course", "Description", 2022, Course.Category.Science, professor);
        course.requestJoin(student);
        course.acceptStudent(student);
        repoCourse.save(course);
        professor.getCoursesTaught().add(course);
        repoUser.save(professor);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        loginPage.loginAsStudent();

        driver.get(baseDomain() + "student/courses/" + course.getId() + "/request-to-join");
        // Check that we're redirected to the list of courses
        assertEquals("/student/courses", currentPath());
    }

    /**
     * Check that searches with bad parameters correctly result in error messages being shown
     */
    @Test
    public void badSearch() {
        String email = "some.student@ettore.it";
        String password = "SomeSecurePassword";
        User student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        loginPage.loginAsStudent();

        driver.get(baseDomain() + "student/courses/search?startingYear=x");
        assertEquals(Set.of("Invalid starting year"), new ErrorsComponent(driver).getErrorMessageSet());

        driver.get(baseDomain() + "student/courses/search?category=x");
        assertEquals(Set.of("Invalid category"), new ErrorsComponent(driver).getErrorMessageSet());
    }
}
