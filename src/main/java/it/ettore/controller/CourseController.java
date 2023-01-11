package it.ettore.controller;

import com.sun.istack.NotNull;
import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
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
        if(!user.isPresent()){
            return "redirect:/login";
        }
        Optional<List<Course>> courses = repoCourse.findByProfessor(user.get());
        model.addAttribute("user",user.get());
        model.addAttribute("courses", courses.isPresent() ? courses.get() : new ArrayList<>());
        return "course/list";

    }

    @RequestMapping(value = "/courses/{id}", method = RequestMethod.GET)
    public String coursesDetails(Model model, @PathVariable @NotNull long id){
        // TODO - get current user
        Optional<User> user = repoUser.findByEmail("a.professor@ettore.it");
        if(!user.isPresent()){
            return "redirect:/login";
        }
        model.addAttribute("user",user.get());
        Optional<Course> course = repoCourse.findById(id);
        // On wrong id redirect to courses
        if(!course.isPresent()){
            return "redirect:/courses";
        }
        model.addAttribute("course", course.get());
        return "course/details";
    }
}
