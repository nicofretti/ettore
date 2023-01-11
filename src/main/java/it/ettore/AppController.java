package it.ettore;

import it.ettore.model.CourseRepository;
import it.ettore.model.User;
import it.ettore.model.UserRepository;
import it.ettore.utils.Utils;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AppController {
    @Autowired
    private UserRepository repoUser;
    @Autowired
    private CourseRepository repoCourse;

    @GetMapping("/index")
    public String indexPage(HttpServletRequest request) {
        User user = Utils.loggedUser(request);
        System.out.println(user);
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam(name="email", required=true) String email,
            @RequestParam(name="password", required=true) String password,
            Model model,
            HttpServletRequest request
    ) {
        Optional<User> user = repoUser.findByEmail(email);
        if (user.isEmpty() || !user.get().getPswHash().equals(User.hashPsw(password))) { //maybe move hashPsw to Utils?
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }

        request.getSession().setAttribute("PSW_HASH", user.get().getPswHash());

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
        Model model,
        HttpServletRequest request
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
                model.addAttribute("error", "Invalid role");
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
                model.addAttribute("error", "Email already taken");
                return "register";
            }
            // Unhandled exception
            throw exc;
        }

        request.getSession().setAttribute("PSW_HASH", user.getPswHash());

        return "redirect:/index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("PSW_HASH");
        return "redirect:/login";
    }
}
