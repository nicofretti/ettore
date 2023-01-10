package it.ettore;

import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import it.ettore.utils.Utils;
import it.ettore.AuthInterceptor.Auth;
import java.util.Optional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppController {
    @Autowired
    private UserRepository repoUser;
    @Autowired
    private CourseRepository repoCourse;

    @GetMapping("/index")
    public String indexPage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    @Auth
    public String login(
            @RequestParam(name="email", required=true) String email,
            @RequestParam(name="password", required=true) String password,
            Model model
    ) {
        Optional<User> user = repoUser.findByEmail(email);
        if (user.isEmpty() || !user.get().getPswHash().equals(User.hashPsw(password))) { //maybe move hashPsw to Utils?
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }

        model.addAttribute("user", user.get());
        if(user.get().getRole() == User.Role.PROFESSOR) {
            model.addAttribute("HomepageProfessor", user.get());
            // TODO to change
            return "redirect:/index";
        } else {
            model.addAttribute("HomepageStudent", user.get());
            // TODO to change
            return "redirect:/index";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
        @RequestParam(name="first_name", required=true) String firstName,
        @RequestParam(name="last_name", required=true) String lastName,
        @RequestParam(name="email", required=true) String email,
        @RequestParam(name="password", required=true) String password,
        @RequestParam(name="role", required=true) String stringRole,
        Model model
    ) {
        User.Role role;
        switch (stringRole) {
            case "student":
                role = User.Role.STUDENT;
                break;
            case "professor":
                role = User.Role.PROFESSOR;
                break;
            default:
                model.addAttribute("error", "Ruolo invalido");
                return "register";
        }

        User user;
        try {
            user = new User(firstName, lastName, email, password, role);
        } catch (IllegalArgumentException exc) {
            model.addAttribute("error", exc.getMessage());
            return "register";
        }

        try {
            repoUser.save(user);
        } catch (Exception exc) {
            if (Utils.IsCause(exc, DataIntegrityViolationException.class)) {
                model.addAttribute("error", "Email non disponibile");
                return "register";
            }
            // Unhandled exception
            throw exc;
        }

        // TODO Send back cookie
        return "redirect:/index";
    }
}
