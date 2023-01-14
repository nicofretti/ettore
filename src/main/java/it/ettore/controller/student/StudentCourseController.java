package it.ettore.controller.student;

import com.sun.istack.NotNull;
import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import it.ettore.utils.Breadcrumb;
import it.ettore.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class StudentCourseController {
    @Autowired
    private CourseRepository repoCourse;
    @Autowired
    private UserRepository repoUser;

    @GetMapping("/student/courses")
    public String coursesPage(Model model, HttpServletRequest request) {
        User student = Utils.loggedUser(request);

        // Add attributes
        model.addAllAttributes(
                Map.of(
                        "user", student,
                        "breadcrumbs", List.of(new Breadcrumb("Courses", "/student/courses")),
                        "courses", repoCourse.findByStudentsJoined(student)
                )
        );

        return "student/courses/list";
    }

    @GetMapping(value = "/student/courses/{id}")
    public String courseDetailsPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request) {
        User student = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/student/courses";
        }
        Course course = maybeCourse.get();

        if (!course.isStudentJoined(student)) {
            Utils.addError(model, "You have not joined this course, or are waiting to be approved");
            return "redirect:/student/courses";
        }

        // Add attributes
        model.addAllAttributes(
                Map.of(
                        "user", student,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/student/courses"),
                                new Breadcrumb(course.getName(), String.format("/student/courses/%d", course.getId()))
                        ),
                        "course", course
                )
        );

        return "student/courses/details";
    }

    @GetMapping(value = "/student/courses/{id}/lessons")
    public String courseLessonsPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request) {
        User student = Utils.loggedUser(request);

        // TODO
        return "todo";
    }

    @GetMapping(value = "/student/courses/{id}/unjoin")
    public String courseUnjoin(@PathVariable @NotNull long id, Model model, HttpServletRequest request) {
        User student = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            return "redirect:/student/courses";
        }
        Course course = maybeCourse.get();

        if (!course.isStudentJoined(student)) {
            Utils.addError(model, "You have not joined this course, or are waiting to be approved");
            return "redirect:/student/courses";
        }

        course.removeStudent(student);
        repoCourse.save(course);

        return "redirect:/student/courses";
    }
}
