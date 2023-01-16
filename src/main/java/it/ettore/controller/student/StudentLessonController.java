package it.ettore.controller.student;

import com.sun.istack.NotNull;
import it.ettore.model.*;
import it.ettore.utils.Breadcrumb;
import it.ettore.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class StudentLessonController {

    @Autowired
    private LessonRepository repoLesson;
    @Autowired
    private CourseRepository repoCourse;

    /**
     * Controlls if the student that is logged in is enrolled in the course
     */
    private boolean checkCourseSubscription(User student, Optional<Course> maybeCourse, RedirectAttributes redirectAttributes) {
        if (maybeCourse.isEmpty()) {
            Utils.addRedirectionError(redirectAttributes, "Course not found");
            return false;
        }
        Course course = maybeCourse.get();
        if (!course.isStudentJoined(student)) {
            Utils.addRedirectionError(redirectAttributes, "You are not subscribed to this course");
            return false;
        }
        return true;
    }

    /**
     * Controlls if the student that is logged in is enrolled in the lesson
     */
    public boolean checkLessonBelongsToCourse(Course course, Optional<Lesson> maybeLesson, RedirectAttributes redirectAttributes) {
        if (maybeLesson.isEmpty()) {
            Utils.addRedirectionError(redirectAttributes, "Lesson not found");
            return false;
        }
        Lesson lesson = maybeLesson.get();
        if (course.getId() != lesson.getCourse().getId()) {
            Utils.addRedirectionError(redirectAttributes, "This lesson is not part of this course");
            return false;
        }
        return true;
    }

    @GetMapping(value = "/student/courses/{id}/lessons")
    public String studentLessonsPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User student = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (!checkCourseSubscription(student, maybeCourse, redirectAttributes)) {
            return "redirect:/student/courses";
        }
        Course course = maybeCourse.get();

        // Add attributes
        model.addAllAttributes(
                Map.of(
                        "user", student,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/student/courses"),
                                new Breadcrumb(course.getName(), String.format("/student/courses/%d", id)),
                                new Breadcrumb("Lessons", String.format("/student/courses/%d/lessons", id))
                        ),
                        "lessons", course.getLessons(),
                        "course", course

                )
        );
        return "student/lessons/list";
    }

    @GetMapping(value = "/student/courses/{id}/lessons/{lessonId}")
    public String studentLessonPage(@PathVariable @NotNull long id, @PathVariable @NotNull long lessonId, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User student = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (!checkCourseSubscription(student, maybeCourse, redirectAttributes)) {
            return "redirect:/student/courses";
        }
        Course course = maybeCourse.get();

        if (!checkLessonBelongsToCourse(course, repoLesson.findById(lessonId), redirectAttributes)) {
            return String.format("redirect:/student/courses/%d/lessons", id);
        }

        Optional<Lesson> maybeLesson = repoLesson.findById(lessonId);
        if (maybeLesson.isEmpty()) {
            return "redirect:/student/lessons";
        }
        Lesson lesson = maybeLesson.get();

        model.addAllAttributes(
                Map.of(
                        "user", student,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/student/courses"),
                                new Breadcrumb(course.getName(), String.format("/student/courses/%d", id)),
                                new Breadcrumb("Lessons", String.format("/student/courses/%d/lessons", id)),
                                new Breadcrumb(lesson.getTitle(), String.format("/student/courses/%d/lessons/%d", id, lessonId))
                        ),
                        "lesson", lesson,
                        "course", course,
                        //boolean for the modify button
                        "canEdit", false
                )
        );
        return "professor/lessons/content";
    }
}
