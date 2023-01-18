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
            Utils.addRedirectionError(redirectAttributes, "You can't add/view/edit/delete lessons for this course because you don't teach it");
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

    /*
     *  Check exists a lesson with the same title, if exists the method adds "(1)" to the title recursively
     * */
    public String correctLessonTitle(String title, List<Lesson> lessons, Optional<Long> allowId) {
        for (Lesson lesson : lessons) {
            if (allowId.isPresent() && lesson.getId() == allowId.get()) {
                continue;
            }

            if (lesson.getTitle().equals(title)) {
                title += "(1)";
                title = correctLessonTitle(title, lessons, allowId);
                break;
            }
        }
        return title;
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
                        // Boolean for the modify button
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

        // Add attributes
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", id)),
                                new Breadcrumb("Lessons", String.format("/professor/courses/%d/lessons", id)),
                                new Breadcrumb("Add", String.format("/professor/courses/%d/lessons/add", id))
                        )
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
        User professor = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (!checkCourseAndProfessorOwnership(professor, maybeCourse, redirectAttributes)) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        // Check if the lesson has same name of another lesson of the same course
        title = correctLessonTitle(title, course.getLessons(), Optional.empty());

        Lesson lesson = new Lesson(title, description.isBlank() ? null : description, content, course);
        repoLesson.save(lesson);

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
                        "lesson", lesson
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

        // Check if the lesson has same name of another lesson of the same course
        title = correctLessonTitle(title, course.getLessons(), Optional.of(lessonId));

        lesson.setTitle(title);
        lesson.setDescription(description.isBlank() ? null : description);
        lesson.setContent(content);
        repoLesson.save(lesson);

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

        Optional<Lesson> maybeLesson = repoLesson.findById(lessonId);
        // Check if lesson is in the course
        if (!checkLessonBelongsToCourse(course, maybeLesson, redirectAttributes)) {
            return String.format("redirect:/professor/courses/%d/lessons", id);
        }
        Lesson lesson = maybeLesson.get();

        repoLesson.delete(lesson);

        return String.format("redirect:/professor/courses/%d/lessons", id);
    }
}
