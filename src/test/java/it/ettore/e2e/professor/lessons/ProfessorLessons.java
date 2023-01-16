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

    Course course;
    Lesson lessonOne;
    Lesson lessonTwo;

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

        ProfessorCoursesPage coursesPage = loginPage.loginAsProfessor();
        ProfessorLessonsPage lessonsPage = coursesPage.getCourses().get(0).goTo().goToLessons();
        assertEquals(2, lessonsPage.getLessons().size());
        assertEquals(String.format("/professor/courses/%d/lessons", course.getId()), currentPath());
    }

    /**
     * Check correctness of breadcrumbs shown in lessons list and lesson detail pages
     */
    @Test
    public void breadcrumbs() {
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
                new Breadcrumb("LESSON NAME", String.format("/professor/courses/%d/lessons/%d", course.getId(), lessonOne.getId()))
        ), lessonPage.headerComponent().getBreadcrumbs());
    }

    /**
     * Test the correctness of the lessons
     */
    @Test
    public void lessonContents() {
        ProfessorLessonsPage lessonsPage = new ProfessorLessonsPage(driver);

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