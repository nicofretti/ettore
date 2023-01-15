package it.ettore.e2e.professor.lessons;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonsPage;
import it.ettore.model.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class ProfessorLesson extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;
    @Autowired
    protected LessonRepository repoLesson;

    ProfessorCoursesPage coursesPage;
    Course course;

    Lesson lessonOne;

    @Before
    public void prepareLessonTests() {
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
        repoLesson.save(lessonOne);

        course.getLessons().add(lessonOne);
        repoCourse.save(course);

        driver.get(baseDomain() + "login");
        LoginPage loginPage = new LoginPage(driver);

        loginPage.setEmail(email);
        loginPage.setPassword(password);

        coursesPage = loginPage.loginAsProfessor();
    }

    @Test
    public void lesson() {
        //click on course
        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();

        // Should be in the details page for the course
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());


        //click on the first lesson
        ProfessorLessonsPage lessonsPage = courseDetails.goToLessons();
        ProfessorLessonPage lessonPage = lessonsPage.getLessons().get(0).goTo();

        // Should be in the details page for the lesson
        assertEquals(String.format("/professor/courses/%d/lessons/%d", course.getId(), lessonOne.getId()), currentPath());

        //check if lesson data is correct
        assertEquals(lessonOne.getTitle(), lessonPage.getTitle());
        assertEquals(lessonOne.getContent(), lessonPage.getContent());
    }
}
