package it.ettore.e2e.professor.courses;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCourseAddPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.model.*;
import it.ettore.utils.Breadcrumb;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProfessorCourseEdit extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;
    @Autowired
    protected LessonRepository repoLesson;

    private Course course;

    @Before
    public void prepareEditTests() {
        // Remove any course that might have been inserted by the DB bootstrapper
        repoCourse.findAll().forEach(course -> {
            repoLesson.deleteAll(repoLesson.findByCourse(course));
            repoCourse.delete(course);
        });

        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
        repoUser.save(professor);

        course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);
        // Link the course to the professor
        professor.getCoursesTaught().add(course);
        repoUser.save(professor);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        loginPage.loginAsProfessor();

        assertEquals(1, new ProfessorCoursesPage(driver).getCourses().size());
    }

    /**
     * Tests all the breadcrumb links when editing a course are correct
     */
    @Test
    public void breadcrumbsWhenEditing() {
        ProfessorCoursesPage coursesPage = new ProfessorCoursesPage(driver);

        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses")
        ), coursesPage.headerComponent().getBreadcrumbs());

        // Click on course
        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();

        // Should be in the details page for the course
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());

        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId()))
        ), courseDetails.headerComponent().getBreadcrumbs());

        // Click on the edit button
        ProfessorCourseAddPage editPage = courseDetails.editCourse();
        assertEquals(String.format("/professor/courses/%d/edit", course.getId()), currentPath());

        //check the breadcrumbs
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("EDIT", String.format("/professor/courses/%d/edit", course.getId()))
        ), editPage.headerComponent().getBreadcrumbs());
    }

    /**
     * Test modifying existing course
     */
    @Test
    public void modifyCourse() {
        ProfessorCoursesPage coursesPage = new ProfessorCoursesPage(driver);
        // Check that there is only one course
        assertEquals(1, coursesPage.getCourses().size());

        // Click on the course details
        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();

        // Click on course
        ProfessorCourseAddPage editCourse = courseDetails.editCourse();

        assertEquals(String.format("/professor/courses/%d/edit", course.getId()), currentPath());

        // Start adding a new course
        editCourse.setCourseName("New course name");
        editCourse.setDescription("New course description");
        editCourse.setStartingYear(2021);
        editCourse.setCategory(Course.Category.Science);

        // Click on the add button
        ProfessorCoursePage coursePage = editCourse.submitCourseEdit();

        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());

        // Check the course details
        assertEquals("New course name", coursePage.getName());
        assertEquals("(2021/2022)", coursePage.getPeriod());
        assertEquals("New course description", coursePage.getDescription());
    }

    /**
     * Test deleting existing course
     */
    @Test
    public void deleteCourse() {
        ProfessorCoursesPage coursesPage = new ProfessorCoursesPage(driver);
        assertEquals("/professor/courses", currentPath());
        assertEquals(1, coursesPage.getCourses().size());

        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());

        // Click on the edit button
        ProfessorCourseAddPage editPage = courseDetails.editCourse();
        assertEquals(String.format("/professor/courses/%d/edit", course.getId()), currentPath());

        // Click on the delete button
        coursesPage = editPage.submitCourseDelete();
        assertEquals("/professor/courses", currentPath());
        assertEquals(0, coursesPage.getCourses().size());
    }
}