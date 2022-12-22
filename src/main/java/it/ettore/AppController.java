package it.ettore;

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

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/user")
    public String addUser(Model model) {
        // This variable contains either the User already present in the database or a newly created one
        User user = repoUser.findById(1).orElseGet(() -> {
            // If there isn't a user already, add one
            User newUser = new User();
            repoUser.save(newUser);
            return newUser;
        });

        model.addAttribute("userId", user.getId());
        return "user";
    }
}
