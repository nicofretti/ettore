package it.ettore;

import it.ettore.model.Course;
import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {
    @Autowired
    private UserRepository repoUser;
    @Autowired
    private CourseRepository repoCourse;

    @RequestMapping("/")
    public String index(Model model) {
        if (repoUser.findById(1).isPresent()) {
            System.out.println("Already present");
            return "index";
        }
        System.out.println("Creating new user");

        User user = new User("Nico", "Frex", "nico@gmail.com", "ACAB", User.Role.PROFESSOR);
        repoUser.save(user);

        Course course = new Course("Fourier", "Bogdan mihai", 2022, Course.Category.Maths, user);
        repoCourse.save(course);

        System.out.println(user.getCoursesTaught() == null);

        // Refresh
        user = repoUser.findById(1).get();

        System.out.println(user.getCoursesTaught() == null);

        // model.addAttribute("user", user.toString());

        return "index";
    }
}
