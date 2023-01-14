package it.ettore.e2e.professor.lessons;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage.CourseComponent;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonsPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonsPage.LessonComponent;
import it.ettore.model.*;
import it.ettore.utils.Breadcrumb;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProfessorLessons extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;
    @Autowired
    protected LessonRepository repoLesson;

    /*Tests all the breadcrumb links from courses page to specific lesson contents page*/
    @Test
    public void breadcrumbs() {
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
    }

    /*Tests the correctness of lesson contents*/
    @Test
    public void lessonContents() {
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

        List<CourseComponent> courses = coursesPage.getCourses();
        assertEquals(1, courses.size());
        assertEquals("Course name", courses.get(0).getName());
        assertEquals("(2023/2024)", courses.get(0).getPeriod());
        assertEquals("Course description", courses.get(0).getDescription());
        ProfessorCoursePage courseDetails = courses.get(0).goTo();

        // Should be in the details page for the course
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());

        //click on the first lesson
        ProfessorLessonsPage lessonsPage = courseDetails.goToLessons();
        assertEquals(String.format("/professor/courses/%d/lessons", course.getId()), currentPath());

        List<LessonComponent> lessons = lessonsPage.getLessons();
        assertEquals(2, lessons.size());
        assertEquals("Lesson name", lessons.get(0).getTitle());
        assertEquals("Lesson description", lessons.get(0).getContent());
        assertEquals("Lesson name 2", lessons.get(1).getTitle());
        assertEquals("Lesson description 2", lessons.get(1).getContent());

        ProfessorLessonPage lessonPage = lessons.get(0).goTo();
        assertEquals(String.format("/professor/courses/%d/lessons/%d", course.getId(), lesson.getId()), currentPath());
        assertEquals("Lesson name", lessonPage.getTitle());
        assertEquals("Lesson content", lessonPage.getContent());
    }
}