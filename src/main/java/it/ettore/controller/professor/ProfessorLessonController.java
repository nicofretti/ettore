package it.ettore.controller.professor;

import com.sun.istack.NotNull;
import it.ettore.model.*;
import it.ettore.utils.Breadcrumb;
import it.ettore.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ProfessorLessonController {
    @Autowired
    private LessonRepository repoLesson;
    @Autowired
    private CourseRepository repoCourse;

    private boolean checkCourseAndProfessorOwnership(User professor, Optional<Course> maybeCourse, RedirectAttributes redirectAttributes) {
        if (maybeCourse.isEmpty()) {
            Utils.addRedirectionError(redirectAttributes, "Course not found");
            return false;
        }
        Course course = maybeCourse.get();
        if (course.getProfessor().getId() != professor.getId()) {
            Utils.addRedirectionError(redirectAttributes, "You can't add a lesson to a course that you don't own");
            return false;
        }
        return true;
    }

    /*
     * Check if course has lesson
     * */
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

    @GetMapping("/professor/courses/{id}/lessons")
    public String lessonsPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (!checkCourseAndProfessorOwnership(professor, maybeCourse, redirectAttributes)) {
            return "redirect:/professor/courses";
        }

        Course course = maybeCourse.get();
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", id)),
                                new Breadcrumb("Lessons", String.format("/professor/courses/%d/lessons", id))
                        ),
                        "lessons", course.getLessons(),
                        "course", course
                )
        );

        return "professor/lessons/list";
    }

    @GetMapping("/professor/courses/{id}/lessons/{lessonId}")
    public String lessonContentPage(@PathVariable @NotNull long id, @PathVariable @NotNull long lessonId, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (!checkCourseAndProfessorOwnership(professor, maybeCourse, redirectAttributes)) {
            return "redirect:/professor/courses";
        }

        Course course = maybeCourse.get();
        Optional<Lesson> maybeLesson = repoLesson.findById(lessonId);
        // Check if lesson is in the course
        if (!checkLessonBelongsToCourse(course, maybeLesson, redirectAttributes)) {
            return String.format("redirect:/professor/courses/%d/lessons", id);
        }

        Lesson lesson = maybeLesson.get();
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", id)),
                                new Breadcrumb("Lessons", String.format("/professor/courses/%d/lessons", id)),
                                new Breadcrumb(lesson.getTitle(), String.format("/professor/courses/%d/lessons/%d", id, lessonId))
                        ),
                        "lesson", lesson,
                        "course", course,
                        //boolean for the modify button
                        "canEdit", true
                )
        );
        return "professor/lessons/content";
    }

    @GetMapping(value = "/professor/courses/{id}/lessons/add")
    public String lessonAddPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (!checkCourseAndProfessorOwnership(professor, maybeCourse, redirectAttributes)) {
            return "redirect:/professor/courses";
        }

        Course course = maybeCourse.get();
        if (course.getProfessor().getId() != professor.getId()) {
            Utils.addRedirectionError(redirectAttributes, "You can't add a lesson to a course that you don't own");
            return "redirect:/professor/courses";
        }

        // Add attributes
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", id)),
                                new Breadcrumb("Lessons", String.format("/professor/courses/%d/lessons", id)),
                                new Breadcrumb("Add", String.format("/professor/courses/%d/lessons/add", id))
                        ),
                        "btnUndo", String.format("/professor/courses/%d/lessons", id)
                )
        );
        return "professor/lessons/add";
    }

    @PostMapping(value = "/professor/courses/{id}/lessons/add")
    public String lessonAdd(@PathVariable @NotNull long id,
                            @RequestParam @NotNull String title,
                            @RequestParam String description,
                            @RequestParam String content,
                            Model model,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        // TODO: lesson with same name
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (!checkCourseAndProfessorOwnership(professor, maybeCourse, redirectAttributes)) {
            return "redirect:/professor/courses";
        }

        Course course = maybeCourse.get();
        Lesson lesson;
        // Errors with lesson parameters
        try {
            lesson = new Lesson(title, description.isBlank() ? null : description, content, course);
        } catch (Exception exc) {
            Utils.addRedirectionError(redirectAttributes, "Parameters errors: " + exc.getClass().getCanonicalName());
            return String.format("redirect:/professor/courses/%d/lessons/add", id);
        }
        // Errors with database
        try {
            repoLesson.save(lesson);
        } catch (Exception exc) {
            if (Utils.IsCause(exc, DataIntegrityViolationException.class)) {
                Utils.addRedirectionError(redirectAttributes, "Lesson already exists");
            } else {
                Utils.addRedirectionError(redirectAttributes, "Error while adding lesson: " + exc.getClass().getCanonicalName());
            }
            redirectAttributes.addFlashAttribute("lesson", lesson);
            return String.format("redirect:/professor/courses/%d/lessons/add", id);
        }
        return String.format("redirect:/professor/courses/%d/lessons", id);
    }

    @GetMapping(value = "/professor/courses/{id}/lessons/{lessonId}/edit")
    public String lessonEditPage(@PathVariable @NotNull long id, @PathVariable @NotNull long lessonId, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (!checkCourseAndProfessorOwnership(professor, maybeCourse, redirectAttributes)) {
            return "redirect:/professor/courses";
        }

        Course course = maybeCourse.get();
        Optional<Lesson> maybeLesson = repoLesson.findById(lessonId);
        // Check if lesson is in the course
        if (!checkLessonBelongsToCourse(course, maybeLesson, redirectAttributes)) {
            return String.format("redirect:/professor/courses/%d/lessons", id);
        }

        Lesson lesson = maybeLesson.get();
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb(maybeCourse.get().getName(), String.format("/professor/courses/%d", id)),
                                new Breadcrumb("Lessons", String.format("/professor/courses/%d/lessons", id)),
                                new Breadcrumb(lesson.getTitle(), String.format("/professor/courses/%d/lessons/%d", id, lessonId)),
                                new Breadcrumb("Edit", String.format("/professor/courses/%d/lessons/%d/edit", id, lessonId))
                        ),
                        "lesson", lesson,
                        "btnUndo", String.format("/professor/courses/%d/lessons/%d/delete", id, lessonId)
                )
        );
        return "professor/lessons/add";
    }

    @PostMapping(value = "/professor/courses/{id}/lessons/{lessonId}/edit")
    public String lessonEdit(@PathVariable @NotNull long id, @PathVariable @NotNull long lessonId,
                             @RequestParam @NotNull String title,
                             @RequestParam String description,
                             @RequestParam String content,
                             Model model,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        // TODO: Lesson with same name
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (!checkCourseAndProfessorOwnership(professor, maybeCourse, redirectAttributes)) {
            return "redirect:/professor/courses";
        }

        Course course = maybeCourse.get();
        Optional<Lesson> maybeLesson = repoLesson.findById(lessonId);
        // Check if lesson is in the course
        if (!checkLessonBelongsToCourse(course, maybeLesson, redirectAttributes)) {
            return String.format("redirect:/professor/courses/%d/lessons", id);
        }

        Lesson lesson = maybeLesson.get();
        // Errors with lesson parameters
        // Errors with database
        try {
            lesson.setTitle(title);
            lesson.setDescription(description.isBlank() ? null : description);
            lesson.setContent(content);
            repoLesson.save(lesson);
        } catch (Exception exc) {
            if (Utils.IsCause(exc, DataIntegrityViolationException.class)) {
                Utils.addRedirectionError(redirectAttributes, "Lesson already exists");
            } else {
                Utils.addRedirectionError(redirectAttributes, "Error while adding lesson: " + exc.getClass().getCanonicalName());
            }
            redirectAttributes.addFlashAttribute("lesson", lesson);
            return String.format("/professor/courses/%d/lessons/%d/edit", id, lessonId);
        }
        return String.format("redirect:/professor/courses/%d/lessons", id);
    }

    @GetMapping(value = "/professor/courses/{id}/lessons/{lessonId}/delete")
    public String lessonDelete(@PathVariable @NotNull long id, @PathVariable @NotNull long lessonId, Model model, HttpServletRequest request
            , RedirectAttributes redirectAttributes) {
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (!checkCourseAndProfessorOwnership(professor, maybeCourse, redirectAttributes)) {
            return "redirect:/professor/courses";
        }

        Course course = maybeCourse.get();
        if (course.getProfessor().getId() != professor.getId()) {
            Utils.addRedirectionError(redirectAttributes, "You can't delete a lesson to a course that you don't own");
            return "redirect:/professor/courses";
        }

        Optional<Lesson> maybeLesson = repoLesson.findById(lessonId);
        // Check if lesson is in the course
        if (!checkLessonBelongsToCourse(course, maybeLesson, redirectAttributes)) {
            return String.format("redirect:/professor/courses/%d/lessons", id);
        }

        repoLesson.delete(maybeLesson.get());

        return String.format("redirect:/professor/courses/%d/lessons", id);
    }
}
