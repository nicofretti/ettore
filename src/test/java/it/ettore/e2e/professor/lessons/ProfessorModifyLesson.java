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

    /*Tests all the breadcrumb links when editing a course are correct*/
    @Test
    public void breadcrumbsWhenEditingLesson() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
        repoUser.save(professor);

        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);
        // Link the course to the professor
        professor.getCoursesTaught().add(course);
        repoUser.save(professor);

        Lesson lesson = new Lesson("Lesson name", "Lesson description", "Lesson content", course);
        Lesson lesson2 = new Lesson("Lesson name 2", "Lesson description 2", "Lesson content 2", course);
        repoLesson.saveAll(List.of(lesson, lesson2));

        course.getLessons().addAll(List.of(lesson, lesson2));
        repoCourse.save(course);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        ProfessorCoursesPage coursesPage = loginPage.loginAsProfessor();
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

        //click on the first lesson
        ProfessorLessonsPage lessonsPage = courseDetails.goToLessons();
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
                new Breadcrumb("LESSON NAME", String.format("/professor/courses/%d/lessons/%d", course.getId(), lesson.getId()))
        ), lessonPage.headerComponent().getBreadcrumbs());

        ProfessorModifyLessonPage modifyLessonPage = lessonPage.editLesson();
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId())),
                new Breadcrumb("LESSON NAME", String.format("/professor/courses/%d/lessons/%d", course.getId(), lesson.getId())),
                new Breadcrumb("EDIT", String.format("/professor/courses/%d/lessons/%d/edit", course.getId(), lesson.getId()))
        ), modifyLessonPage.headerComponent().getBreadcrumbs());
    }

    /*Tests all the breadcrumb links when adding a new course */
    @Test
    public void breadcrumbsWhenNewLesson() {
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
        ProfessorModifyLessonPage editLesson = courseDetails.editLesson();
        assertEquals(String.format("/professor/courses/%d/edit", course.getId()), currentPath());

        //check the breadcrumbs
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("EDIT", String.format("/professor/courses/%d/edit", course.getId()))
        ), editLesson.headerComponent().getBreadcrumbs());
    }


    /*Tests the creation of a new lesson*/
    @Test
    public void addNewLesson() {
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
        assertEquals("/professor/courses", currentPath());

        //click on first course
        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();
        assertEquals(String.format("/professor/courses/%d", professor.getCoursesTaught().get(0).getId()), currentPath());

        //click lessons
        ProfessorLessonsPage lessonsPage = courseDetails.goToLessons();
        assertEquals(String.format("/professor/courses/%d/lessons", professor.getCoursesTaught().get(0).getId()), currentPath());
        assertEquals(0, lessonsPage.getLessons().size());

        //click on add new lesson
        ProfessorModifyLessonPage modifyLessonPage = lessonsPage.newLesson();
        assertEquals(String.format("/professor/courses/%d/lessons/add", professor.getCoursesTaught().get(0).getId()), currentPath());

        //fill the form
        modifyLessonPage.setTitle("Lesson name");
        modifyLessonPage.setDescription("Lesson description");
        modifyLessonPage.setContent("# Lesson content");

        //click on save
        ProfessorLessonsPage lessonsPage2 = modifyLessonPage.saveLesson();
        assertEquals(1, lessonsPage2.getLessons().size());
    }

    /* Test modifying existing course */
    @Test
    public void modifyCourse() {
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
        assertEquals("/professor/courses", currentPath());

        //click on first course
        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();
        assertEquals(String.format("/professor/courses/%d", professor.getCoursesTaught().get(0).getId()), currentPath());

        //click lessons
        ProfessorLessonsPage lessonsPage = courseDetails.goToLessons();
        assertEquals(String.format("/professor/courses/%d/lessons", professor.getCoursesTaught().get(0).getId()), currentPath());
        assertEquals(0, lessonsPage.getLessons().size());

        //click on add new lesson
        ProfessorModifyLessonPage modifyLessonPage = lessonsPage.newLesson();
        assertEquals(String.format("/professor/courses/%d/lessons/add", professor.getCoursesTaught().get(0).getId()), currentPath());

        //fill the form
        modifyLessonPage.setTitle("Lesson name");
        modifyLessonPage.setDescription("Lesson description");
        modifyLessonPage.setContent("# Lesson content");

        //click on save
        ProfessorLessonsPage lessonsPage2 = modifyLessonPage.saveLesson();
        assertEquals(1, lessonsPage2.getLessons().size());

        //click on lesson
        ProfessorLessonPage lessonPage = lessonsPage2.getLessons().get(0).goTo();

        //click on edit
        ProfessorModifyLessonPage modifyLessonPage2 = lessonPage.editLesson();

        //fill the form
        modifyLessonPage2.setTitle("Lesson name 2");
        modifyLessonPage2.setDescription("Lesson description 2");
        modifyLessonPage2.setContent("# Lesson content 2");

        //click on save
        ProfessorLessonsPage lessonPage2 = modifyLessonPage2.saveLesson();
        //assertEquals(1, lessonPage2.getLessons().size());


    }

    /* Test deleting existing lesson */
    @Test
    public void deleteLesson() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
        repoUser.save(professor);

        Course course = new Course("Course name", "Course description", 2023, Course.Category.Maths, professor);
        repoCourse.save(course);

        Lesson lesson = new Lesson("Lesson name", "Lesson description", "# Lesson content", course);
        repoLesson.save(lesson);

        course.getLessons().add(lesson);
        repoCourse.save(course);

        // Link the course to the professor
        professor.getCoursesTaught().add(course);
        repoUser.save(professor);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        ProfessorCoursesPage coursesPage = loginPage.loginAsProfessor();
        assertEquals("/professor/courses", currentPath());

        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());

        ProfessorLessonsPage lessonsPage = courseDetails.goToLessons();
        assertEquals(String.format("/professor/courses/%d/lessons", course.getId()), currentPath());
        assertEquals(1, lessonsPage.getLessons().size());

        ProfessorLessonPage lessonPage = lessonsPage.getLessons().get(0).goTo();
        assertEquals(String.format("/professor/courses/%d/lessons/%d", course.getId(), lesson.getId()), currentPath());

        ProfessorModifyLessonPage modifyLessonPage = lessonPage.editLesson();

        lessonsPage = modifyLessonPage.cancelLesson();
        assertEquals(String.format("/professor/courses/%d/lessons", course.getId()), currentPath());
        assertEquals(0, lessonsPage.getLessons().size());
    }

}