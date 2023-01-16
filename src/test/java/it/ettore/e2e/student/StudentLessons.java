package it.ettore.e2e.student;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LessonDetailsPage;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage.CourseComponent;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonsPage.LessonComponent;
import it.ettore.e2e.po.student.StudentCoursePage;
import it.ettore.e2e.po.student.StudentCoursesPage;
import it.ettore.e2e.po.student.StudentLessonsPage;
import it.ettore.model.*;
import it.ettore.utils.Breadcrumb;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class StudentLessons extends E2EBaseTest {
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
        String email = "some.student@ettore.it";
        String password = "SomeSecurePassword";
        User student = new User("Some", "Student", email, password, User.Role.STUDENT);
        repoUser.save(student);

        course = new Course("Course name", "Course description", 2023, Course.Category.Maths, student);
        course.requestJoin(student);
        course.acceptStudent(student);
        repoCourse.save(course);        // Link the course to the student

        lessonOne = new Lesson("Lesson name", "Lesson description", "Lesson content", course);
        lessonTwo = new Lesson("Lesson name 2", "Lesson description 2", "Lesson content 2", course);
        repoLesson.saveAll(List.of(lessonOne, lessonTwo));

        course.getLessons().addAll(List.of(lessonOne, lessonTwo));
        repoCourse.save(course);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        StudentCoursesPage coursesPage = loginPage.loginAsStudent();
        StudentLessonsPage lessonsPage = coursesPage.getCourses().get(0).goTo().goToLessons();
        assertEquals(2, lessonsPage.getLessons().size());
        assertEquals(String.format("/student/courses/%d/lessons", course.getId()), currentPath());
    }

    /**
     * Tests all the breadcrumb links from courses page to specific lesson contents page
     */
    @Test
    public void breadcrumbs() {
        StudentLessonsPage lessonsPage = new StudentLessonsPage(driver);
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/student/courses"),
                new Breadcrumb("COURSE NAME", String.format("/student/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/student/courses/%d/lessons", course.getId()))
        ), lessonsPage.headerComponent().getBreadcrumbs());

        LessonDetailsPage lessonPage = lessonsPage.getLessons().get(0).goTo();
        assertEquals(List.of(
                new Breadcrumb("COURSES", "/student/courses"),
                new Breadcrumb("COURSE NAME", String.format("/student/courses/%d", course.getId())),
                new Breadcrumb("LESSONS", String.format("/student/courses/%d/lessons", course.getId())),
                new Breadcrumb("LESSON NAME", String.format("/student/courses/%d/lessons/%d", course.getId(), lessonOne.getId()))
        ), lessonPage.headerComponent().getBreadcrumbs());
    }
}