package it.ettore.e2e.professor.lessons;

import it.ettore.e2e.E2EBaseTest;
import it.ettore.e2e.po.LoginPage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursePage;
import it.ettore.e2e.po.professor.courses.ProfessorCoursesPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonPage;
import it.ettore.e2e.po.professor.lessons.ProfessorLessonsPage;
import it.ettore.model.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProfessorLesson extends E2EBaseTest {
    @Autowired
    protected UserRepository repoUser;
    @Autowired
    protected CourseRepository repoCourse;
    @Autowired
    protected LessonRepository repoLesson;

    @Test
    public void lesson() {
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

        //click on course
        ProfessorCoursePage courseDetails = coursesPage.getCourses().get(0).goTo();

        // Should be in the details page for the course
        assertEquals(String.format("/professor/courses/%d", course.getId()), currentPath());


        //click on the first lesson
        ProfessorLessonsPage lessonsPage = courseDetails.goToLessons();
        ProfessorLessonPage lessonPage = lessonsPage.getLessons().get(0).goTo();

        // Should be in the details page for the lesson
        assertEquals(String.format("/professor/courses/%d/lessons/%d", course.getId(), lesson.getId()), currentPath());

        //check if lesson data is correct
        assertEquals("Lesson name", lessonPage.getTitle());
        assertEquals("Lesson content", lessonPage.getContent());
    }
}
