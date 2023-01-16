package it.ettore.e2e.professor.lessons;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage.CourseComponent;
import it.ettore.e2e.po.LessonDetailsPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonsPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonsPage.LessonComponent;
import it.ettore.model.*;
import it.ettore.utils.Breadcrumb;
import org.junit.Before;
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
    Course course;
    Lesson lessonOne;
    Lesson lessonTwo;
    ProfessorCoursesPage coursesPage;

    @Before
    public void prepareLessonsTests() {
        String email = "some.professor@ettore.it";
        String password = "SomeSecurePassword";
        User professor = new User("Some", "Professor", email, password, User.Role.PROFESSOR);
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
    }

    @Test
    public void breadcrumbs() {
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

        LessonDetailsPage lessonPage = lessonsPage.getLessons().get(0).goTo();
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/professor/courses"),
                new Breadcrumb("COURSE NAME", String.format("/professor/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/professor/courses/%d/lessons", course.getId())),
                new Breadcrumb("LESSON NAME", String.format("/professor/courses/%d/lessons/%d", course.getId(), lessonOne.getId()))
        ), lessonPage.headerComponent().getBreadcrumbs());
    }

    /*Tests the correctness of lesson contents*/
    @Test
    public void lessonContents() {
        List<CourseComponent> courses = coursesPage.getCourses();
        assertEquals(1, courses.size());
        assertEquals(course.getName(), courses.get(0).getName());
        assertEquals(course.formatPeriod(), courses.get(0).getPeriod());
        assertEquals(course.getDescription(), courses.get(0).getDescription());
        ProfessorCoursePage courseDetails = courses.get(0).goTo();

        // Should be in the details page for the course
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());

        //click on the first lesson
        ProfessorLessonsPage lessonsPage = courseDetails.goToLessons();
        assertEquals(String.format("/professor/courses/%d/lessons", course.getId()), currentPath());

        List<LessonComponent> lessons = lessonsPage.getLessons();
        assertEquals(2, lessons.size());
        assertEquals(lessonOne.getTitle(), lessons.get(0).getTitle());
        assertEquals(lessonOne.getDescription(), lessons.get(0).getDescription());
        assertEquals(lessonTwo.getTitle(), lessons.get(1).getTitle());
        assertEquals(lessonTwo.getDescription(), lessons.get(1).getDescription());

        LessonDetailsPage lessonPage = lessons.get(0).goTo();
        assertEquals(String.format("/professor/courses/%d/lessons/%d", course.getId(), lessonOne.getId()), currentPath());
        assertEquals(lessonOne.getTitle(), lessonPage.getTitle());
        assertEquals(lessonOne.getContent(), lessonPage.getContent());
    }
}