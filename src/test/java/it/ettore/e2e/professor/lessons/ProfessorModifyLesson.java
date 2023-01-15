package it.ettore.e2e.professor.lessons;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonsPage;
import it.ettore.e2e.po.professor.lessons.ProfessorModifyLessonPage;
import it.ettore.model.*;
import it.ettore.utils.Breadcrumb;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProfessorModifyLesson extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;
    @Autowired
    protected LessonRepository repoLesson;

    /*Tests all the breadcrumb links from courses page to specific lesson contents page*/
    User professor;
    Course course;
    Lesson lessonOne;
    Lesson lessonTwo;
    ProfessorCoursesPage coursesPage;
    ProfessorCoursePage courseDetails;
    ProfessorLessonsPage lessonsPage;

    @Before
    public void prepareLessonModifierTests() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
        repoUser.save(professor);

        course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);
        // Link the course to the professor
        professor.getCoursesTaught().add(course);
        repoUser.save(professor);

        lessonOne = new Lesson("Lesson name", "Lesson description", "Lesson content", course);
        lessonTwo = new Lesson("Lesson name 2", "Lesson description 2", "Lesson content 2", course);
        repoLesson.saveAll(List.of(lessonOne, lessonTwo));

        course.getLessons().addAll(List.of(lessonOne, lessonTwo));
        repoCourse.save(course);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        coursesPage = loginPage.loginAsProfessor();

        //click on course
        courseDetails = coursesPage.getCourses().get(0).goTo();
        //click on the first lesson
        lessonsPage = courseDetails.goToLessons();
    }


    /*Tests all the breadcrumb links when editing a course are correct*/
    @Test
    public void breadcrumbsWhenEditingLesson() {
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId()))
        ), lessonsPage.headerComponent().getBreadcrumbs());

        ProfessorLessonPage lessonPage = lessonsPage.getLessons().get(0).goTo();
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId())),
                new Breadcrumb("LESSON NAME", String.format("/professor/courses/%d/lessons/%d", course.getId(), lessonOne.getId()))
        ), lessonPage.headerComponent().getBreadcrumbs());

        ProfessorModifyLessonPage modifyLessonPage = lessonPage.editLesson();
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId())),
                new Breadcrumb("LESSON NAME", String.format("/professor/courses/%d/lessons/%d", course.getId(), lessonOne.getId())),
                new Breadcrumb("EDIT", String.format("/professor/courses/%d/lessons/%d/edit", course.getId(), lessonOne.getId()))
        ), modifyLessonPage.headerComponent().getBreadcrumbs());
    }

    /*Tests all the breadcrumb links when adding a new course */
    @Test
    public void breadcrumbsWhenNewLesson() {
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId()))
        ), lessonsPage.headerComponent().getBreadcrumbs());

        ProfessorModifyLessonPage modifyLessonPage = lessonsPage.newLesson();
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId())),
                new Breadcrumb("ADD", String.format("/professor/courses/%d/lessons/add", course.getId()))
        ), modifyLessonPage.headerComponent().getBreadcrumbs());
    }


    /*Tests the creation of a new lesson*/
    @Test
    public void addNewLesson() {
        assertEquals(String.format("/professor/courses/%d/lessons", professor.getCoursesTaught().get(0).getId()), currentPath());
        assertEquals(course.getLessons().size(), lessonsPage.getLessons().size());

        //click on add new lesson
        ProfessorModifyLessonPage modifyLessonPage = lessonsPage.newLesson();
        assertEquals(String.format("/professor/courses/%d/lessons/add", professor.getCoursesTaught().get(0).getId()), currentPath());

        //fill the form
        modifyLessonPage.setTitle("Lesson name");
        modifyLessonPage.setContent("# Lesson content");
        modifyLessonPage.setDescription("Lesson description");

        //click on save
        ProfessorLessonsPage lessonsPage2 = modifyLessonPage.saveLesson();
        assertEquals(course.getLessons().size() + 1, lessonsPage2.getLessons().size());
    }

    /*Test the correctness of adding new lesson*/
    @Test
    public void addNewLessonCorrectness() {
        ProfessorModifyLessonPage modifyLessonPage = lessonsPage.newLesson();
        modifyLessonPage.setTitle("Lesson name");
        modifyLessonPage.setContent("# Lesson content");
        modifyLessonPage.setDescription("Lesson description");
        ProfessorLessonsPage lessonsPage2 = modifyLessonPage.saveLesson();

        assertEquals(course.getLessons().size() + 1, lessonsPage2.getLessons().size());
        assertEquals("Lesson name", lessonsPage2.getLessons().get(0).getTitle());
        assertEquals("Lesson description", lessonsPage2.getLessons().get(0).getDescription());

        //click on last lesson in the list to verify if also the content is correct
        ProfessorLessonPage lessonPage = lessonsPage2.getLessons().get(course.getLessons().size()).goTo();
        assertEquals("# Lesson content", lessonPage.getContent());
    }

    /* Test modifying existing course */
    @Test
    public void modifyCourse() {
        assertEquals(String.format("/professor/courses/%d/lessons", professor.getCoursesTaught().get(0).getId()), currentPath());
        assertEquals(course.getLessons().size(), lessonsPage.getLessons().size());

        //click on add new lesson
        ProfessorModifyLessonPage modifyLessonPage = lessonsPage.newLesson();
        assertEquals(String.format("/professor/courses/%d/lessons/add", professor.getCoursesTaught().get(0).getId()), currentPath());

        //fill the form
        modifyLessonPage.setTitle("Lesson name");
        modifyLessonPage.setContent("# Lesson content");
        modifyLessonPage.setDescription("Lesson description");

        //click on save
        ProfessorLessonsPage lessonsPage2 = modifyLessonPage.saveLesson();
        assertEquals(course.getLessons().size() + 1, lessonsPage2.getLessons().size());

        //click on lesson
        ProfessorLessonPage lessonPage = lessonsPage2.getLessons().get(0).goTo();

        //click on edit
        ProfessorModifyLessonPage modifyLessonPage2 = lessonPage.editLesson();

        //fill the formLesson
        modifyLessonPage2.setTitle("New name 2");
        modifyLessonPage2.setContent("# Lesson content 2");
        modifyLessonPage2.setDescription("New Lesson description 2");

        //click on save
        lessonsPage2 = modifyLessonPage2.saveLesson();

        assertEquals(course.getLessons().size() + 1, lessonsPage2.getLessons().size());
    }

    /* Test modifying existing course correctness */
    @Test
    public void modifyCourseCorrectness() {
        ProfessorLessonPage lessonPage = lessonsPage.getLessons().get(0).goTo();

        //click on edit
        ProfessorModifyLessonPage modifyLessonPage2 = lessonPage.editLesson();

        //fill the formLesson
        modifyLessonPage2.setTitle("New name 2");
        modifyLessonPage2.setContent("# Lesson content 2");
        modifyLessonPage2.setDescription("New Lesson description 2");

        //click on save
        lessonsPage = modifyLessonPage2.saveLesson();

        assertEquals(course.getLessons().size(), lessonsPage.getLessons().size());
        assertEquals("New name 2", lessonsPage.getLessons().get(0).getTitle());
        assertEquals("New Lesson description 2", lessonsPage.getLessons().get(0).getDescription());

        //click on last lesson in the list to verify if also the content is correct
        ProfessorLessonPage lessonPage2 = lessonsPage.getLessons().get(0).goTo();
        assertEquals("# Lesson content 2", lessonPage2.getContent());
    }

    /* Test deleting existing lesson */
    @Test
    public void deleteLesson() {
        assertEquals(String.format("/professor/courses/%d/lessons", course.getId()), currentPath());
        assertEquals(course.getLessons().size(), lessonsPage.getLessons().size());

        ProfessorLessonPage lessonPage = lessonsPage.getLessons().get(0).goTo();
        assertEquals(String.format("/professor/courses/%d/lessons/%d", course.getId(), lessonOne.getId()), currentPath());

        ProfessorModifyLessonPage modifyLessonPage = lessonPage.editLesson();

        lessonsPage = modifyLessonPage.cancelLesson();
        assertEquals(String.format("/professor/courses/%d/lessons", course.getId()), currentPath());
        assertEquals(course.getLessons().size() - 1, lessonsPage.getLessons().size());
    }

    /*Testing that no anomalies happen when modifying procedure is not completed correctly */
    @Test
    public void professorDecidesToLogout() {
        assertEquals(course.getLessons().size(), lessonsPage.getLessons().size());
        ProfessorModifyLessonPage modifyLessonPage = lessonsPage.newLesson();
        modifyLessonPage.setTitle("Lesson name");
        modifyLessonPage.setDescription("Lesson description");
        modifyLessonPage.setContent("# Lesson content");

        LoginPage loginPage = modifyLessonPage.headerComponent().logout();
        loginPage.setEmail("some.professor@ettore.it");
        loginPage.setPassword("SomeSecurePassword");
        coursesPage = loginPage.loginAsProfessor();
        courseDetails = coursesPage.getCourses().get(0).goTo();
        lessonsPage = courseDetails.goToLessons();
        assertEquals(course.getLessons().size(), lessonsPage.getLessons().size());
    }

}