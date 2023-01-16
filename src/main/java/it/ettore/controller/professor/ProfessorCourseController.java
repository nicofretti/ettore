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
public class ProfessorCourseController {
    @Autowired
    private CourseRepository repoCourse;
    @Autowired
    private UserRepository repoUser;
    @Autowired
    private LessonRepository repoLesson;

    @GetMapping("/professor/courses")
    public String coursesPage(Model model, HttpServletRequest request) {
        User professor = Utils.loggedUser(request);

        Iterable<Course> coursesTaught = repoCourse.findByProfessor(professor);
        // Add attributes
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(new Breadcrumb("Courses", "/professor/courses")),
                        "courses", coursesTaught
                )
        );
        return "professor/courses/list";
    }

    @GetMapping(value = "/professor/courses/{id}")
    public String courseDetailsPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User professor = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        if (course.getProfessor().getId() != professor.getId()) {
            Utils.addRedirectionError(redirectAttributes, "This course is not taught by you");
            return "redirect:/professor/courses";
        }

        // Add attributes
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", id))
                        ),
                        "course", course
                )
        );

        return "professor/courses/details";
    }

    @GetMapping(value = "/professor/courses/{id}/manage")
    public String courseManagePage(@PathVariable @NotNull long id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User professor = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        if (course.getProfessor().getId() != professor.getId()) {
            Utils.addRedirectionError(redirectAttributes, "This course is not taught by you");
            return "redirect:/professor/courses";
        }

        // Add attributes
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", id)),
                                new Breadcrumb("Manage", String.format("/professor/courses/%d/manage", id))
                        ),
                        "course", course,
                        "studentsRequesting", course.getStudentsRequesting(),
                        "studentsJoined", course.getStudentsJoined()
                )
        );

        return "professor/courses/manage";
    }

    @GetMapping(value = "/professor/courses/{id}/accept/{studentId}")
    public String courseAcceptStudent(@PathVariable @NotNull long id, @PathVariable @NotNull long studentId, Model model, HttpServletRequest request) {
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        Optional<User> maybeStudent = repoUser.findById(studentId);
        // On wrong ID, redirect to course manage page as if nothing had happened
        if (maybeStudent.isEmpty()) {
            return String.format("redirect:/professor/courses/%d/manage", id);
        }
        User student = maybeStudent.get();

        course.acceptStudent(student);
        repoCourse.save(course);

        return String.format("redirect:/professor/courses/%d/manage", id);
    }

    @GetMapping(value = "/professor/courses/{id}/reject/{studentId}")
    public String courseRejectStudent(@PathVariable @NotNull long id, @PathVariable @NotNull long studentId, Model model, HttpServletRequest request) {
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        Optional<User> maybeStudent = repoUser.findById(studentId);
        // On wrong ID, redirect to course manage page as if nothing had happened
        if (maybeStudent.isEmpty()) {
            return String.format("redirect:/professor/courses/%d/manage", id);
        }
        User student = maybeStudent.get();

        course.rejectStudent(student);
        repoCourse.save(course);

        return String.format("redirect:/professor/courses/%d/manage", id);
    }

    @GetMapping(value = "/professor/courses/{id}/remove/{studentId}")
    public String courseRemoveStudent(@PathVariable @NotNull long id, @PathVariable @NotNull long studentId, Model model, HttpServletRequest request) {
        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        Optional<User> maybeStudent = repoUser.findById(studentId);
        // On wrong ID, redirect to course manage page as if nothing had happened
        if (maybeStudent.isEmpty()) {
            return String.format("redirect:/professor/courses/%d/manage", id);
        }
        User student = maybeStudent.get();

        course.removeStudent(student);
        repoCourse.save(course);

        return String.format("redirect:/professor/courses/%d/manage", id);
    }

    @GetMapping(value = "/professor/courses/add")
    public String courseAddPage(Model model, HttpServletRequest request) {
        User professor = Utils.loggedUser(request);
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb("Add", "/professor/courses/add")
                        ),
                        "title", "Add course"
                )
        );

        return "professor/courses/add";
    }

    @PostMapping(value = "/professor/courses/add")
    public String courseAdd(
            @RequestParam @NotNull String name,
            @RequestParam String description,
            @RequestParam @NotNull Integer startingYear,
            @RequestParam @NotNull String category,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        User professor = Utils.loggedUser(request);

        Course course = new Course(name, description.isBlank() ? null : description, startingYear, Course.Category.fromString(category), professor);

        try {
            repoCourse.save(course);
        } catch (Exception exc) {
            if (Utils.IsCause(exc, DataIntegrityViolationException.class)) {
                Utils.addRedirectionError(redirectAttributes, "Course already exists");
            }
            redirectAttributes.addFlashAttribute("course", course);
            return "redirect:/professor/courses/add";
        }

        return "redirect:/professor/courses";
    }

    @GetMapping(value = "/professor/courses/{id}/edit")
    public String courseEditPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User professor = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            Utils.addRedirectionError(redirectAttributes, "No such course");
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        if (course.getProfessor().getId() != professor.getId()) {
            Utils.addRedirectionError(redirectAttributes, "This course is not taught by you");
            return "redirect:/professor/courses";
        }

        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", id)),
                                new Breadcrumb("Edit", String.format("/professor/courses/%d/edit", id))
                        ),
                        "title", "Edit course",
                        "course", course
                )
        );

        // The "add" template is also used for edits. The "course" attribute in the model helps the template to
        // understand that an existing course is being edited instead of creating a new one
        return "professor/courses/add";
    }

    @PostMapping(value = "/professor/courses/{id}/edit")
    public String courseEdit(
            @PathVariable @NotNull long id,
            @RequestParam @NotNull String name,
            @RequestParam String description,
            @RequestParam @NotNull Integer startingYear,
            @RequestParam @NotNull String category,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        User professor = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            Utils.addRedirectionError(redirectAttributes, "No such course");
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        if (course.getProfessor().getId() != professor.getId()) {
            Utils.addRedirectionError(redirectAttributes, "This course is not taught by you");
            return "redirect:/professor/courses";
        }

        course.setName(name);
        course.setDescription(description.isBlank() ? null : description);
        course.setStartingYear(startingYear);
        course.setCategory(Course.Category.fromString(category));
        repoCourse.save(course);

        return String.format("redirect:/professor/courses/%d", id);
    }


    @GetMapping(value = "/professor/courses/{id}/delete")
    public String courseDelete(@PathVariable @NotNull long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User professor = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            Utils.addRedirectionError(redirectAttributes, "No such course");
            return "redirect:/professor/courses";
        }
        Course course = maybeCourse.get();

        if (course.getProfessor().getId() != professor.getId()) {
            Utils.addRedirectionError(redirectAttributes, "This course is not taught by you");
            return "redirect:/professor/courses";
        }

        repoLesson.deleteAll(course.getLessons());
        repoCourse.delete(course);

        return "redirect:/professor/courses";
    }
}
