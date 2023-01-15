package it.ettore.e2e.professor.courses;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.ErrorsComponent;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCourseAddPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.model.*;
import it.ettore.utils.Breadcrumb;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ProfessorCourseAdd extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;
    @Autowired
    protected LessonRepository repoLesson;

    @Before
    public void prepareAddTests() {
        // Remove any course that might have been inserted by the DB bootstrapper
        repoCourse.findAll().forEach(course -> {
            repoLesson.deleteAll(repoLesson.findByCourse(course));
            repoCourse.delete(course);
        });

        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
        repoUser.save(professor);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        loginPage.loginAsProfessor();

        assertEquals(0, new ProfessorCoursesPage(driver).getCourses().size());
    }

    /**
     * Tests all the breadcrumb links when adding a new course
     */
    @Test
    public void breadcrumbsWhenNewCourse() {
        ProfessorCoursesPage coursesPage = new ProfessorCoursesPage(driver);

        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses")
        ), coursesPage.headerComponent().getBreadcrumbs());

        // Click on new course button
        ProfessorCourseAddPage addCoursePage = coursesPage.newCourse();

        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("ADD", "/professor/courses/add")
        ), addCoursePage.headerComponent().getBreadcrumbs());
    }


    /**
     * Tests the creation of new courses
     */
    @Test
    public void addNewCourse() {
        ProfessorCoursesPage coursesPage = new ProfessorCoursesPage(driver);
        // Check that there is no course
        assertEquals(0, coursesPage.getCourses().size());

        // Click on add new course
        ProfessorCourseAddPage addCoursePage = coursesPage.newCourse();
        assertEquals("/professor/courses/add", currentPath());

        //start adding a new course
        addCoursePage.setCourseName("New course name");
        addCoursePage.setDescription("New course description");
        addCoursePage.setStartingYear(2023);
        addCoursePage.setCategory(Course.Category.Science);

        //click on the add button
        coursesPage = addCoursePage.submitCourseAdd();
        assertEquals("/professor/courses", currentPath());
        assertEquals(1, coursesPage.getCourses().size());

        //check the course details
        assertEquals("New course name", coursesPage.getCourses().get(0).getName());
        assertEquals("(2023/2024)", coursesPage.getCourses().get(0).getPeriod());
        assertEquals("New course description", coursesPage.getCourses().get(0).getDescription());
    }

    /**
     * Tests that it is not possible to create course with a name that is already taken and an error message is shown if
     * this is attempted
     */
    @Test
    public void twoCoursesSameNameIsForbidden() {
        ProfessorCoursesPage coursesPage = new ProfessorCoursesPage(driver);
        // Check that there is no course
        assertEquals(0, coursesPage.getCourses().size());

        // Add course 1
        ProfessorCourseAddPage addCoursePage = coursesPage.newCourse();
        assertEquals("/professor/courses/add", currentPath());
        addCoursePage.setCourseName("Course");
        coursesPage = addCoursePage.submitCourseAdd();

        assertEquals("/professor/courses", currentPath());

        // Add course 2
        addCoursePage = coursesPage.newCourse();
        assertEquals("/professor/courses/add", currentPath());
        addCoursePage.setCourseName("Course");
        addCoursePage.submitCourseAdd();

        // Should still be in the /add page
        assertEquals("/professor/courses/add", currentPath());
        addCoursePage.refresh();

        // Check that the error message is shown
        ErrorsComponent errorsComponent = new ErrorsComponent(driver);
        assertEquals(Set.of("Course already exists"), errorsComponent.getErrorMessageSet());

        // Check that the form is still populated after this failed attempt
        assertEquals("Course", addCoursePage.getCourseName());
    }

    /**
     * Tests that the "Save" button, in the page for creating a new course, is not clickable unless the name is not blank
     */
    @Test
    public void saveButtonNotClickableOnEmptyName() {
        ProfessorCoursesPage coursesPage = new ProfessorCoursesPage(driver);
        // Check that there is no course
        assertEquals(0, coursesPage.getCourses().size());

        ProfessorCourseAddPage addCoursePage = coursesPage.newCourse();
        assertEquals("/professor/courses/add", currentPath());
        // Initially not clickable
        assertFalse(addCoursePage.isSaveButtonClickable());
        addCoursePage.setCourseName("Some name");
        assertTrue(addCoursePage.isSaveButtonClickable());
        // Let's see if the button returns un-clickable when the user clears the input
        addCoursePage.clearName();
        assertFalse(addCoursePage.isSaveButtonClickable());
        addCoursePage.setCourseName("Some name");
        assertTrue(addCoursePage.isSaveButtonClickable());
        coursesPage = addCoursePage.submitCourseAdd();

        assertEquals("/professor/courses", currentPath());
        // Assert that we did actually create a new course
        assertEquals(1, coursesPage.getCourses().size());
    }
}
