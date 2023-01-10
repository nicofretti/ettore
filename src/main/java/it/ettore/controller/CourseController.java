package it.ettore.controller;

import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class CourseController {
    @Autowired
    private CourseRepository repoCourse;

    @Autowired
    private UserRepository repoUser;

    @GetMapping("/courses")
    public String coursesList(Model model){
        // TODO - get current user
        User user = repoUser.findByEmail("a.professor@ettore.it").get();
        Optional<List<Course>> courses = repoCourse.findByProfessor(user);
        model.addAttribute("user",user);
        model.addAttribute("courses", courses.get());
        return "course/list";

    }
}
