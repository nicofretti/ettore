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

    @GetMapping(value = "/professor/courses/add")
    public String courseAddPage(Model model, HttpServletRequest request){
        User professor = Utils.loggedUser(request);
        model.addAllAttributes(
                Map.of(
                        "user", professor,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/professor/courses"),
                                new Breadcrumb("Add", "/professor/courses/add")
                        )
                )
        );
        return "professor/courses/add";
    }
}
