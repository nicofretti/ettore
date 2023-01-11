package it.ettore.controller;

import com.sun.istack.NotNull;
import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import it.ettore.utils.Breadcrumb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@Controller
public class CourseController {
    @Autowired
    private CourseRepository repoCourse;

    @Autowired
    private UserRepository repoUser;

    @GetMapping("/courses")
    public String coursesList(Model model){
        // TODO - get current user
        Optional<User> user = repoUser.findByEmail("a.professor@ettore.it");
        if(user.isEmpty()){
            return "redirect:/login";
        }
        Optional<List<Course>> courses = repoCourse.findByProfessor(user.get());
        // Add attributes
        model.addAllAttributes(
            Map.of(
                "breadcrumbs", List.of(new Breadcrumb("I miei corsi", "/courses/")),
                "user", user.get(),
                "courses", courses.orElseGet(ArrayList::new)
            )
        );
        return "course/list";
    }

    @RequestMapping(value = "/courses/{id}", method = RequestMethod.GET)
    public String coursesDetails(Model model, @PathVariable @NotNull long id){
        // TODO - get current user
        Optional<User> user = repoUser.findByEmail("a.professor@ettore.it");
        if(user.isEmpty()){
            return "redirect:/login";
        }
        Optional<Course> course = repoCourse.findById(id);
        // On wrong id redirect to courses
        if(course.isEmpty()){
            return "redirect:/courses";
        }
        // Add attributes
        model.addAllAttributes(
            Map.of(
                "breadcrumbs", Arrays.asList(
                    new Breadcrumb("I miei corsi","/courses/"),
                    new Breadcrumb(course.get().getName(),"/courses/"+course.get().getId()+"/")
                ),
                "user", user.get(),
                "course", course.get()
            )
        );
        return "course/details";
    }
}
