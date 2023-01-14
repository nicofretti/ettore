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

    @GetMapping("/professor/courses/{id}/lessons")
    public String lessonsPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request) {
        User professor = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        // Add attributes
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
    public String lessonContentPage(@PathVariable @NotNull long id, @PathVariable @NotNull long lessonId, Model model, HttpServletRequest request) {
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }

        Optional<Lesson> maybeLesson = repoLesson.findById(lessonId);
        if (maybeLesson.isEmpty()) {
            return "redirect:/professor/lessons";
        }
        Lesson lesson = maybeLesson.get();
        Course course = lesson.getCourse();

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
    public String lessonAddPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request) {
        User professor = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();
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
                            HttpServletRequest request) {
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();
        Lesson lesson;
        // Errors with lesson parameters
        try {
            lesson = new Lesson(title, description.isBlank() ? null : description, content, course);
        } catch (Exception exc) {
            Utils.addError(model, "Parameters errors: " + exc.getClass().getCanonicalName());
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
            return String.format("/professor/courses/%d/lessons/add", id);
        }
        // Errors with database
        try {
            repoLesson.save(lesson);
        } catch (Exception exc) {
            if (Utils.IsCause(exc, DataIntegrityViolationException.class)) {
                Utils.addError(model, "Lesson already exists");
            } else {
                Utils.addError(model, "Error while adding lesson: " + exc.getClass().getCanonicalName());
            }
            model.addAllAttributes(
                    Map.of(
                            "user", professor,
                            "breadcrumbs", List.of(
                                    new Breadcrumb("Courses", "/professor/courses"),
                                    new Breadcrumb(course.getName(), String.format("/professor/courses/%d", id)),
                                    new Breadcrumb("Lessons", String.format("/professor/courses/%d/lessons/add", id))
                            ),
                            "error", "Course already exists",
                            "course", course,
                            "btnUndo", "/professor/courses"
                    )
            );
            return String.format("/professor/courses/%d/lessons/add", id);
        }
        return String.format("redirect:/professor/courses/%d/lessons", id);
    }

    @GetMapping(value = "/professor/courses/{id}/lessons/{lessonId}/edit")
    public String lessonEditPage(@PathVariable @NotNull long id, @PathVariable @NotNull long lessonId, Model model, HttpServletRequest request) {
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses/";
        }
        Optional<Lesson> maybeLesson = repoLesson.findById(lessonId);
        // On wrong ID, redirect to lessons list
        if (maybeLesson.isEmpty()) {
            return String.format("redirect:/professor/courses/%d/lessons", id);
        }
        User professor = Utils.loggedUser(request);
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
                             HttpServletRequest request) {
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        Optional<Lesson> maybeLesson = repoLesson.findById(lessonId);
        // On wrong ID, redirect to lessons list
        if (maybeLesson.isEmpty()) {
            return String.format("redirect:/professor/courses/%d/lessons", id);
        }
        Lesson lesson = maybeLesson.get();
        // Errors with lesson parameters
        try {
            lesson.setTitle(title);
            lesson.setDescription(description.isBlank() ? null : description);
            lesson.setContent(content);
        } catch (Exception exc) {
            Utils.addError(model, "Parameters errors: " + exc.getClass().getCanonicalName());
            model.addAllAttributes(
                    Map.of(
                            "user", professor,
                            "breadcrumbs", List.of(
                                    new Breadcrumb("Courses", "/professor/courses"),
                                    new Breadcrumb(course.getName(), String.format("/professor/courses/%d", id)),
                                    new Breadcrumb("Lessons", String.format("/professor/courses/%d/lessons", id)),
                                    new Breadcrumb("Edit", String.format("/professor/courses/%d/lessons/%d/edit", id, lessonId))
                            ),
                            "btnUndo", String.format("/professor/courses/%d/lessons", id)
                    )
            );
            return String.format("/professor/courses/%d/lessons/add", id);
        }
        // Errors with database
        try {
            repoLesson.save(lesson);
        } catch (Exception exc) {
            if (Utils.IsCause(exc, DataIntegrityViolationException.class)) {
                Utils.addError(model, "Lesson already exists");
            } else {
                Utils.addError(model, "Error while adding lesson: " + exc.getClass().getCanonicalName());
            }
            model.addAllAttributes(
                    Map.of(
                            "user", professor,
                            "breadcrumbs", List.of(
                                    new Breadcrumb("Courses", "/professor/courses"),
                                    new Breadcrumb(course.getName(), String.format("/professor/courses/%d", id)),
                                    new Breadcrumb("Edit", String.format("/professor/courses/%d/lessons/%d/edit", id, lessonId))
                            ),
                            "error", "Course already exists",
                            "course", course,
                            "btnUndo", String.format("/professor/courses/%d/lessons/%d/delete", id, lessonId)

                    )
            );
            return String.format("/professor/courses/%d/lessons/%d/edit", id, lessonId);
        }
        return String.format("redirect:/professor/courses/%d/lessons/%d", id, lessonId);
    }

    @GetMapping(value = "/professor/courses/{id}/lessons/{lessonId}/delete")
    public String lessonDelete(@PathVariable @NotNull long id, @PathVariable @NotNull long lessonId, Model model, HttpServletRequest request) {
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        Optional<Lesson> maybeLesson = repoLesson.findById(lessonId);
        // On wrong ID, redirect to lessons list
        if (maybeLesson.isEmpty()) {
            return String.format("redirect:/professor/courses/%d/lessons", id);
        }
        try {
            repoLesson.delete(maybeLesson.get());
        } catch (Exception exc) {
            Utils.addError(model, "Error while deleting lesson: " + exc.getClass().getCanonicalName());
            model.addAllAttributes(
                    Map.of(
                            "user", professor,
                            "breadcrumbs", List.of(
                                    new Breadcrumb("Courses", "/professor/courses"),
                                    new Breadcrumb(course.getName(), String.format("/professor/courses/%d", id)),
                                    new Breadcrumb("Edit", String.format("/professor/courses/%d/lessons/%d/edit", id, lessonId))
                            ),
                            "error", "Course already exists",
                            "course", course,
                            "btnUndo", String.format("/professor/courses/%d/lessons/%d/delete", id, lessonId)
                    )
            );
            return String.format("/professor/courses/%d/lessons/%d/edit", id, lessonId);
        }
        return String.format("redirect:/professor/courses/%d/lessons", id);
    }
}
