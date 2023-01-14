package it.ettore.controller.professor;

import com.sun.istack.NotNull;
import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import it.ettore.utils.Breadcrumb;
import it.ettore.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class ProfessorCourseController {
    @Autowired
    private CourseRepository repoCourse;
    @Autowired
    private UserRepository repoUser;

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
    public String courseDetailsPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request) {
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
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", course.getId()))
                        ),
                        "course", course
                )
        );

        return "professor/courses/details";
    }

    @GetMapping(value = "/professor/courses/{id}/manage")
    public String courseManagePage(@PathVariable @NotNull long id, Model model, HttpServletRequest request) {
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
                                new Breadcrumb(course.getName(), String.format("/professor/courses/%d", course.getId())),
                                new Breadcrumb("Manage", String.format("/professor/courses/%d/manage", course.getId()))
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
        User professor = Utils.loggedUser(request);

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
        User professor = Utils.loggedUser(request);

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
        User professor = Utils.loggedUser(request);

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
}
