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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

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
    public String coursesPage(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
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
    public String courseDetailsPage(@PathVariable @NotNull long id, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User student = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            Utils.addRedirectionError(redirectAttributes, "No such course");
            return "redirect:/student/courses";
        }
        Course course = maybeCourse.get();

        if (!course.isStudentJoined(student)) {
            Utils.addRedirectionError(redirectAttributes, "You have not joined this course, or are waiting to be approved");
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
    public String courseUnjoin(@PathVariable @NotNull long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User student = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            Utils.addRedirectionError(redirectAttributes, "No such course");
            return "redirect:/student/courses";
        }
        Course course = maybeCourse.get();

        if (!course.isStudentJoined(student)) {
            Utils.addRedirectionError(redirectAttributes, "NYou have not joined this course, or are waiting to be approved");
            return "redirect:/student/courses";
        }

        course.removeStudent(student);
        repoCourse.save(course);

        return "redirect:/student/courses";
    }

    @GetMapping("/student/courses/search")
    public String search(
            Model model,
            HttpServletRequest request,
            @RequestParam(name = "text", defaultValue = "") String text,
            @RequestParam(name = "startingYear", defaultValue = "") String startingYearStr,
            @RequestParam(name = "category", defaultValue = "") String categoryStr
    ) {
        User student = Utils.loggedUser(request);

        List<Course> courses = new ArrayList<>();
        repoCourse.findAll().forEach(courses::add);

        // Apply text search filters, ignore case
        courses.removeIf(course -> !course.getName().toLowerCase().contains(text.toLowerCase()) && !course.getDescription().toLowerCase().contains(text.toLowerCase()));

        // If there is a starting year filter, retain only the matching courses
        if (!startingYearStr.isEmpty()) {
            try {
                int startingYear = Integer.parseInt(startingYearStr);
                courses.removeIf(course -> course.getStartingYear() != startingYear);
            } catch (NumberFormatException exc) {
                Utils.addError(model, "Invalid starting year");
            }
        }

        // If there is a category filter, retain only the matching courses
        if (!categoryStr.isEmpty()) {
            Course.Category category = Course.Category.fromString(categoryStr);
            if (category == null) {
                Utils.addError(model, "Invalid category");
            } else {
                courses.removeIf(course -> course.getCategory() != category);
            }
        }

        courses.forEach(course -> {
            if (course.getStudentsRequesting().stream().anyMatch(someStudent -> someStudent.getId() == student.getId())) {
                course.hasRequestedAlready = true;
            }
            if (course.getStudentsJoined().stream().anyMatch(someStudent -> someStudent.getId() == student.getId())) {
                course.hasJoinedAlready = true;
            }
        });

        // Add attributes
        model.addAllAttributes(
                Map.of(
                        "user", student,
                        "breadcrumbs", List.of(
                                new Breadcrumb("Courses", "/student/courses"),
                                new Breadcrumb("Search", "/student/courses/search")
                        ),
                        "courses", courses,
                        "initialCategory", categoryStr,
                        "initialText", text,
                        "initialStartingYear", startingYearStr
                )
        );

        return "student/courses/search";
    }

    @GetMapping(value = "/student/courses/{id}/request-to-join")
    public String requestToJoinCourse(@PathVariable @NotNull long id, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User student = Utils.loggedUser(request);

        Optional<Course> maybeCourse = repoCourse.findById(id);
        // On wrong ID, redirect to courses list
        if (maybeCourse.isEmpty()) {
            Utils.addRedirectionError(redirectAttributes, "No such course");
            return "redirect:/student/courses";
        }
        Course course = maybeCourse.get();

        if (course.isStudentRequesting(student)) {
            Utils.addRedirectionError(redirectAttributes, "You have have already requested to join this course");
            return "redirect:/student/courses";
        }

        if (course.isStudentJoined(student)) {
            Utils.addRedirectionError(redirectAttributes, "You have have already joined this course");
            return "redirect:/student/courses";
        }

        course.requestJoin(student);
        repoCourse.save(course);

        return "redirect:/student/courses";
    }
}
