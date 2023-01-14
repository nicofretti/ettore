package it.ettore.e2e.professor.courses;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorAddsCoursePage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.model.*;
import it.ettore.utils.Breadcrumb;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProfessorModifyCourses extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;
    @Autowired
    protected LessonRepository repoLesson;

    Course course;

    ProfessorCoursesPage coursesPage;

    @Before
    public void prepareModifyCoursesTests() {
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

        coursesPage = loginPage.loginAsProfessor();
    }

    /*Tests all the breadcrumb links when editing a course are correct*/
    @Test
    public void breadcrumbsWhenEditing() {
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses")
        ), coursesPage.headerComponent().getBreadcrumbs());

        //click on course
        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();

        // Should be in the details page for the course
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());

        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId()))
        ), courseDetails.headerComponent().getBreadcrumbs());

        //click on the edit button
        ProfessorAddsCoursePage editCourse = courseDetails.editCourse();
        assertEquals(String.format("/professor/courses/%d/edit", course.getId()), currentPath());

        //check the breadcrumbs
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("EDIT", String.format("/professor/courses/%d/edit", course.getId()))
        ), editCourse.headerComponent().getBreadcrumbs());
    }

    /*Tests all the breadcrumb links when adding a new course */
    @Test
    public void breadcrumbsWhenNewCourse() {
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses")
        ), coursesPage.headerComponent().getBreadcrumbs());

        //click on course
        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();

        // Should be in the details page for the course
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());

        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId()))
        ), courseDetails.headerComponent().getBreadcrumbs());

        //click on the edit button
        ProfessorAddsCoursePage editCourse = courseDetails.editCourse();
        assertEquals(String.format("/professor/courses/%d/edit", course.getId()), currentPath());

        //check the breadcrumbs
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("EDIT", String.format("/professor/courses/%d/edit", course.getId()))
        ), editCourse.headerComponent().getBreadcrumbs());
    }


    /*Tests the creation of new courses*/
    @Test
    public void addNewCourse() {
        //check that there are only one course
        assertEquals(1, coursesPage.getCourses().size());

        //click on add new course
        ProfessorAddsCoursePage newCourse = coursesPage.newCourse();
        assertEquals("/professor/courses/add", currentPath());

        //start adding a new course
        newCourse.setCourseName("New course name");
        newCourse.setDescription("New course description");
        newCourse.setStartingYear("2023");
        newCourse.selectCategory("Science");

        //click on the add button
        coursesPage = newCourse.save();
        assertEquals("/professor/courses", currentPath());
        assertEquals(2, coursesPage.getCourses().size());

        //check the course details
        assertEquals("New course name", coursesPage.getCourses().get(coursesPage.getCourses().size() - 1).getName());
        assertEquals("(2023/2024)", coursesPage.getCourses().get(coursesPage.getCourses().size() - 1).getPeriod());
        assertEquals("New course description", coursesPage.getCourses().get(coursesPage.getCourses().size() - 1).getDescription());

    }

    /* Test modifying existing course */
    @Test
    public void modifyCourse() {
        assertEquals("/professor/courses", currentPath());
        assertEquals(1, coursesPage.getCourses().size());

        //click on the course details
        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();

        //click on course
        ProfessorAddsCoursePage editCourse = courseDetails.editCourse();

        //assertEquals(String.format("/professor/courses/%d/edit", course.getId()), currentPath());

        //start adding a new course
        editCourse.setCourseName("New course name");
        editCourse.setDescription("New course description");
        editCourse.setStartingYear("2023");
        editCourse.selectCategory("Science");

        //click on the add button
        coursesPage = editCourse.save();

        //assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());

        //check the course details
        assertEquals("New course name", coursesPage.getCourses().get(coursesPage.getCourses().size() - 2).getName());
        assertEquals("(2023/2024)", coursesPage.getCourses().get(coursesPage.getCourses().size() - 2).getPeriod());
        assertEquals("New course description", coursesPage.getCourses().get(coursesPage.getCourses().size() - 2).getDescription());

        //now there are two courses
        assertEquals(2, coursesPage.getCourses().size());
    }

    /* Test deleting existing course */
    @Test
    public void deleteCourse() {
        assertEquals("/professor/courses", currentPath());
        assertEquals(1, coursesPage.getCourses().size());

        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());

        //click on the edit button
        ProfessorAddsCoursePage editCourse = courseDetails.editCourse();
        assertEquals(String.format("/professor/courses/%d/edit", course.getId()), currentPath());

        //click on the delete button
        coursesPage = editCourse.cancel();
        assertEquals("/professor/courses", currentPath());
        assertEquals(0, coursesPage.getCourses().size());
    }

}