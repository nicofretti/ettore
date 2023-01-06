package it.ettore;

import it.ettore.model.CourseRepository;
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
        return "index";
    }
}
