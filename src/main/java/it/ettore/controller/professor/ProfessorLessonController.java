package it.ettore.controller.professor;

import it.ettore.model.Course;
import it.ettore.model.CourseRepository;

import com.sun.istack.NotNull;
import it.ettore.model.*;

import it.ettore.utils.Breadcrumb;
import it.ettore.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfessorLessonController {
    @Autowired
    private LessonRepository repoLesson;
    @Autowired
    private CourseRepository repoCourse;
    @Autowired
    private UserRepository repoUser;

    @GetMapping("/professor/courses/{id}/lessons")
    public String lessonsPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request) {
        User professor = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        List<Lesson> lessons = course.getLessons();

        // Add attributes
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", course.getId())),
                                new Breadcrumb("Lessons", String.format("/professor/courses/%d/lessons", course.getId()))
                        ),
                        "lessons", lessons,
                        "course", course
                )
        );

        return "professor/lessons/list";
    }

    @GetMapping("/professor/courses/{id}/lessons/{lessonId}")
    public String lessonContentPage(@PathVariable @NotNull long id, @PathVariable @NotNull long lessonId, Model model, HttpServletRequest request) {
        User professor = Utils.loggedUser(request);

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
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", course.getId())),
                                new Breadcrumb("Lessons", String.format("/professor/courses/%d/lessons", course.getId())),
                                new Breadcrumb(lesson.getTitle(), String.format("/professor/courses/%d/lessons/%d", course.getId(), lesson.getId()))
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
            return "redirect:/professor/lessons";
        }
        Course course = maybeCourse.get();

        List<Lesson> lessons = course.getLessons();

        // Add attributes
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", course.getId())),
                                new Breadcrumb("Lessons", String.format("/professor/courses/%d/lessons", course.getId()))
                        ),
                        "lessons", lessons,
                        "course", course
                )
        );
        return "professor/lessons/add";
    }

    @PostMapping(value = "/professor/courses/{id}/lessons/add")
    public String lessonAddPage(@PathVariable @NotNull long id,
                                @RequestParam @NotNull String title,
                                @RequestParam String description,
                                @RequestParam String content,
                                Model model,
                                HttpServletRequest request) {
        User professor = Utils.loggedUser(request);
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/lessons";
        }
        Course course = maybeCourse.get();


        Lesson lesson = new Lesson(title, description.isBlank() ? null : description,content.isBlank() ? null : content, course);

        try {
            repoLesson.save(lesson);
        } catch (Exception exc) {
            model.addAllAttributes(
                    Map.of(
                            "user", professor,
                            "breadcrumbs", List.of(
                                    new Breadcrumb("Courses", "/professor/courses"),
                                    new Breadcrumb(course.getName(), String.format("/professor/courses/%d", course.getId())),
                                    new Breadcrumb("Lessons", String.format("/professor/courses/%d/lessons/add", course.getId()))
                            ),
                            "error", "Course already exists",
                            "course", course,
                            "btnUndo", "/professor/courses"
                    )
            );
            return "professor/lessons/add";
        }

        return String.format("redirect:/professor/courses/%d", course.getId());
    }

//    @GetMapping(value = "/professor/courses/{id}/edit")
//    public String courseEditPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request) {
//        User professor = Utils.loggedUser(request);
//
//        Optional<Course> maybeCourse = repoCourse.findById(id);
//        // On wrong ID, redirect to courses list
//        if (maybeCourse.isEmpty()) {
//            return "redirect:/professor/courses";
//        }
//        Course course = maybeCourse.get();
//
//        model.addAllAttributes(
//                Map.of(
//                        "user", professor,
//                        "breadcrumbs", List.of(
//                                new Breadcrumb("Courses", "/professor/courses"),
//                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", course.getId())),
//                                new Breadcrumb("Edit", String.format("/professor/courses/%d/edit", course.getId()))
//                        ),
//                        "course", course,
//                        "btnUndo", String.format("/professor/courses/%d/delete", course.getId())
//                )
//        );
//
//        return "professor/courses/add";
//    }
//
//    @PostMapping(value = "/professor/courses/{id}/edit")
//    public String courseEdit(@PathVariable @NotNull long id,
//                             @RequestParam @NotNull String name,
//                             @RequestParam String description,
//                             @RequestParam @NotNull Integer startingYear,
//                             @RequestParam @NotNull String category,
//                             Model model,
//                             HttpServletRequest request) {
//        User professor = Utils.loggedUser(request);
//
//        Optional<Course> maybeCourse = repoCourse.findById(id);
//        // On wrong ID, redirect to courses list
//        if (maybeCourse.isEmpty()) {
//            return "redirect:/professor/courses";
//        }
//        Course course = maybeCourse.get();
//
//        course.setName(name);
//        course.setDescription(description.isBlank() ? null : description);
//        course.setStartingYear(startingYear);
//        course.setCategory(Course.Category.fromString(category));
//
//        try {
//            repoCourse.save(course);
//        } catch (Exception exc) {
//            model.addAllAttributes(
//                    Map.of(
//                            "user", professor,
//                            "breadcrumbs", List.of(
//                                    new Breadcrumb("Courses", "/professor/courses"),
//                                    new Breadcrumb(course.getName(), String.format("/professor/courses/%d", course.getId())),
//                                    new Breadcrumb("Edit", String.format("/professor/courses/%d/edit", course.getId()))
//                            ),
//                            "error", "Course already exists",
//                            "course", course,
//                            "btnUndo", String.format("/professor/courses/%d/delete", course.getId())
//                    )
//            );
//            return String.format("professor/courses/%d/edit", course.getId());
//        }
//
//        return String.format("redirect:/professor/courses/%d", course.getId());
//    }
//
//
//    @GetMapping(value = "/professor/courses/{id}/delete")
//    public String courseDelete(@PathVariable @NotNull long id, Model model, HttpServletRequest request) {
//        Optional<Course> maybeCourse = repoCourse.findById(id);
//        // On wrong ID, redirect to courses list
//        if (maybeCourse.isEmpty()) {
//            return "redirect:/professor/courses";
//        }
//        Course course = maybeCourse.get();
//
//        repoLesson.deleteAll(course.getLessons());
//        repoCourse.delete(course);
//        repoCourse.delete(maybeCourse.get());
//
//        return "redirect:/professor/courses";
//    }


}
