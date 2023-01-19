package it.ettore.e2e.professor.lessons;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LessonDetailsPage;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonsPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonAddPage;
import it.ettore.model.*;
import it.ettore.utils.Breadcrumb;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class ProfessorLessonEdit extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;
    @Autowired
    protected LessonRepository repoLesson;

    String email = "some.professor@ettore.it";
    String password = "SomeSecurePassword";
    User professor;

    Course course;
    Lesson lessonOne;
    Lesson lessonTwo;

    /**
     * Preparation step before each test in this class
     */
    @Before
    public void prepareLessonModifierTests() {
        professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
        repoUser.save(professor);

        course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);
        // Link the course to the professor
        professor.getCoursesTaught().add(course);
        repoUser.save(professor);

        lessonOne = new Lesson("# Lesson name 1", "Lesson description 1", "Lesson content", course);
        lessonTwo = new Lesson("# Lesson name 2", "Lesson description 2", "Lesson content 2", course);
        repoLesson.saveAll(List.of(lessonOne, lessonTwo));

        course.getLessons().addAll(List.of(lessonOne, lessonTwo));
        repoCourse.save(course);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        ProfessorCoursesPage coursesPage = loginPage.loginAsProfessor();

        // Click on course
        ProfessorCoursePage coursePage = coursesPage.getCourses().get(0).goTo();
        // Go to the lessons page
        ProfessorLessonsPage lessonsPage = coursePage.goToLessons();

        assertEquals(String.format("/professor/courses/%d/lessons", professor.getCoursesTaught().get(0).getId()), currentPath());

        // Check that the displayed lessons are as many as we created in the DB before
        assertEquals(course.getLessons().size(), lessonsPage.getLessons().size());
    }


    /**
     * Tests all the breadcrumbs are correct when editing a lesson
     */
    @Test
    public void breadcrumbsWhenEditingLesson() {
        ProfessorLessonsPage lessonsPage = new ProfessorLessonsPage(driver);
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId()))
        ), lessonsPage.headerComponent().getBreadcrumbs());

        LessonDetailsPage lessonPage = lessonsPage.getLessons().get(0).goTo();
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId())),
                new Breadcrumb("# LESSON NAME 1", String.format("/professor/courses/%d/lessons/%d", course.getId(), lessonOne.getId()))
        ), lessonPage.headerComponent().getBreadcrumbs());

        ProfessorLessonAddPage modifyLessonPage = lessonPage.editLesson();
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId())),
                new Breadcrumb("# LESSON NAME 1", String.format("/professor/courses/%d/lessons/%d", course.getId(), lessonOne.getId())),
                new Breadcrumb("EDIT", String.format("/professor/courses/%d/lessons/%d/edit", course.getId(), lessonOne.getId()))
        ), modifyLessonPage.headerComponent().getBreadcrumbs());
    }

    /**
     * Tests all the breadcrumbs when adding a new lesson are correct
     */
    @Test
    public void breadcrumbsWhenCreatingLesson() {
        ProfessorLessonsPage lessonsPage = new ProfessorLessonsPage(driver);
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId()))
        ), lessonsPage.headerComponent().getBreadcrumbs());

        ProfessorLessonAddPage modifyLessonPage = lessonsPage.newLesson();
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId())),
                new Breadcrumb("ADD", String.format("/professor/courses/%d/lessons/add", course.getId()))
        ), modifyLessonPage.headerComponent().getBreadcrumbs());
    }


    /**
     * Tests the creation of a new lesson
     */
    @Test
    public void addNewLesson() {
        ProfessorLessonsPage lessonsPage = new ProfessorLessonsPage(driver);

        // Click button to add a lew lesson
        ProfessorLessonAddPage modifyLessonPage = lessonsPage.newLesson();
        assertEquals(String.format("/professor/courses/%d/lessons/add", course.getId()), currentPath());

        // Fill the form
        modifyLessonPage.setTitle("A freshly created lesson's title");
        modifyLessonPage.setContent("# A freshly created lesson's content");
        modifyLessonPage.setDescription("A freshly created lesson's description");

        // Click on save
        lessonsPage = modifyLessonPage.saveLesson();
        // Check that there is a new lesson
        assertEquals(course.getLessons().size() + 1, lessonsPage.getLessons().size());
    }

    /**
     * Try adding two lessons with the same title
     */
    @Test
    public void addSameLessonTwice() {
        ProfessorLessonsPage lessonsPage = new ProfessorLessonsPage(driver);

        // Add first lesson
        ProfessorLessonAddPage newLessonPage = lessonsPage.newLesson();
        newLessonPage.setTitle("Lesson name");
        newLessonPage.setContent("# Lesson content");
        newLessonPage.setDescription("Lesson description");
        lessonsPage = newLessonPage.saveLesson();

        assertEquals(course.getLessons().size() + 1, lessonsPage.getLessons().size());

        // Add second lesson
        newLessonPage = lessonsPage.newLesson();
        newLessonPage.setTitle("Lesson name");
        newLessonPage.setContent("# Lesson content");
        newLessonPage.setDescription("Lesson description");
        lessonsPage = newLessonPage.saveLesson();

        assertEquals(course.getLessons().size() + 2, lessonsPage.getLessons().size());

        // Since the lesson has the same name of the previous now the lesson has name "Lesson name(1)"
        assertEquals("Lesson name", lessonsPage.getLessons().get(course.getLessons().size()).getTitle());
        assertEquals("Lesson name(1)", lessonsPage.getLessons().get(course.getLessons().size() + 1).getTitle());
    }

    /**
     * Tests that Save button is disabled when we have empty fields
     */
    @Test
    public void saveButtonDisabledWhenEmptyFields() {
        ProfessorLessonsPage lessonsPage = new ProfessorLessonsPage(driver);
        ProfessorLessonAddPage newLessonPage = lessonsPage.newLesson();

        assertFalse(newLessonPage.isSaveButtonClickable());
        newLessonPage.setTitle("Lesson name");
        assertFalse(newLessonPage.isSaveButtonClickable());
        newLessonPage.setContent("# Lesson content");
        assertTrue(newLessonPage.isSaveButtonClickable());
        newLessonPage.clearContent();
        assertFalse(newLessonPage.isSaveButtonClickable());
        newLessonPage.setContent("# Lesson content");
        assertTrue(newLessonPage.isSaveButtonClickable());
    }

    /**
     * Create a new lesson, and also check that the title, description and content take on the expected value
     */
    @Test
    public void addNewLessonCorrectness() {
        ProfessorLessonsPage lessonsPage = new ProfessorLessonsPage(driver);

        // Click button to add a lew lesson
        ProfessorLessonAddPage modifyLessonPage = lessonsPage.newLesson();
        assertEquals(String.format("/professor/courses/%d/lessons/add", course.getId()), currentPath());

        // Fill the form
        modifyLessonPage.setTitle("A freshly created lesson's title");
        modifyLessonPage.setContent("# A freshly created lesson's content");
        modifyLessonPage.setDescription("A freshly created lesson's description");

        // Click on save
        lessonsPage = modifyLessonPage.saveLesson();
        // Check that there is a new lesson
        assertEquals(course.getLessons().size() + 1, lessonsPage.getLessons().size());

        // From now on is what's different from test "addNewLesson"

        assertEquals("A freshly created lesson's title", lessonsPage.getLessons().get(course.getLessons().size()).getTitle());
        assertEquals("A freshly created lesson's description", lessonsPage.getLessons().get(course.getLessons().size()).getDescription());

        // Click on last lesson in the list to verify if also the content is correct
        LessonDetailsPage lessonPage = lessonsPage.getLessons().get(course.getLessons().size()).goTo();
        assertEquals("# A freshly created lesson's content", lessonPage.getContent());
    }

    /**
     * Test modification of existing lesson
     */
    @Test
    public void editLesson() {
        ProfessorLessonsPage lessonsPage = new ProfessorLessonsPage(driver);
        LessonDetailsPage lessonPage = lessonsPage.getLessons().get(0).goTo();
        // Check that we're talking about the lesson that we've created before
        assertEquals(lessonOne.getTitle(), lessonPage.getTitle());

        // Click on edit
        ProfessorLessonAddPage editLessonPage = lessonPage.editLesson();

        // Fill the formLesson
        editLessonPage.setTitle("A new new");
        editLessonPage.setContent("# A new content");
        editLessonPage.setDescription("A new description");

        // Click on save
        lessonsPage = editLessonPage.saveLesson();

        // Shouldn't have any new lessons
        assertEquals(course.getLessons().size(), lessonsPage.getLessons().size());
    }

    /**
     * Modidy lesson, and then also check that the title, description and content take on the expected values
     */
    @Test
    public void editLessonCorrectness() {
        ProfessorLessonsPage lessonsPage = new ProfessorLessonsPage(driver);
        LessonDetailsPage lessonPage = lessonsPage.getLessons().get(0).goTo();
        // Check that we're talking about the lesson that we've created before
        assertEquals(lessonOne.getTitle(), lessonPage.getTitle());

        // Click on edit
        ProfessorLessonAddPage editLessonPage = lessonPage.editLesson();

        // Fill the formLesson
        editLessonPage.setTitle("A new name");
        editLessonPage.setContent("# A new content");
        editLessonPage.setDescription("A new description");

        // Click on save
        lessonsPage = editLessonPage.saveLesson();

        // Shouldn't have any new lessons
        assertEquals(course.getLessons().size(), lessonsPage.getLessons().size());

        // From now on is what's different from test "editLesson"

        assertEquals("A new name", lessonsPage.getLessons().get(0).getTitle());
        assertEquals("A new description", lessonsPage.getLessons().get(0).getDescription());

        //click on last lesson in the list to verify if also the content is correct
        lessonPage = lessonsPage.getLessons().get(0).goTo();
        assertEquals("# A new content", lessonPage.getContent());
    }

    /**
     * Delete lesson
     */
    @Test
    public void deleteLesson() {
        ProfessorLessonsPage lessonsPage = new ProfessorLessonsPage(driver);
        LessonDetailsPage lessonPage = lessonsPage.getLessons().get(0).goTo();
        ProfessorLessonAddPage modifyLessonPage = lessonPage.editLesson();

        lessonsPage = modifyLessonPage.deleteLesson();

        assertEquals(String.format("/professor/courses/%d/lessons", course.getId()), currentPath());
        assertEquals(course.getLessons().size() - 1, lessonsPage.getLessons().size());
    }

    /**
     * Testing that no anomalies happen when modifying procedure is not completed correctly
     */
    @Test
    public void professorDecidesToLogout() {
        ProfessorLessonsPage lessonsPage = new ProfessorLessonsPage(driver);
        ProfessorLessonAddPage editLessonPage = lessonsPage.newLesson();

        editLessonPage.setTitle("Lesson name");
        editLessonPage.setDescription("Lesson description");
        editLessonPage.setContent("# Lesson content");

        LoginPage loginPage = editLessonPage.headerComponent().logout();
        loginPage.setEmail(email);
        loginPage.setPassword(password);
        ProfessorCoursesPage coursesPage = loginPage.loginAsProfessor();

        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();
        lessonsPage = courseDetails.goToLessons();

        // There shouldn't be a new lesson because we did not click on the "Save" button
        assertEquals(course.getLessons().size(), lessonsPage.getLessons().size());
    }
}