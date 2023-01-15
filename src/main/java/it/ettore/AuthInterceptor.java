package it.ettore;

import it.ettore.model.User;
import it.ettore.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.Set;

/**
 * An interceptor that allows all whitelisted requests to pass-through, while all other requests need to be
 * authenticated. The logged-in user object is then added to the request parameters so that the actual handler can
 * access it.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private UserRepository repoUser;

    private Optional<User> getLoggedUser(HttpServletRequest request) {
        Object emailObj = request.getSession().getAttribute("ETTORE_EMAIL");
        Object pswHashObj = request.getSession().getAttribute("ETTORE_PSW_HASH");

        if (!(emailObj instanceof String) || !(pswHashObj instanceof String)) {
            return Optional.empty();
        }

        String email = (String) emailObj;
        String pswHash = (String) pswHashObj;

        // Find the user with the matching email and check that the password hash is correct
        Optional<User> user = repoUser.findByEmail(email);
        if (user.isEmpty() || !user.get().getPswHash().equals(pswHash)) {
            return Optional.empty();
        }

        return user;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Optional<User> user = getLoggedUser(request);

        if (user.isEmpty()) {
            // If user is not logged in but wants to go to any of the following URLs, then that's ok. If he/she is
            // trying to go to any other page, redirect to /login
            if (Set.of(
                    "/style.css",
                    "/register",
                    "/login",
                    "/logout"
            ).contains(request.getServletPath())) {
                return HandlerInterceptor.super.preHandle(request, response, handler);
            } else {
                response.sendRedirect("/login");
                return false;
            }
        } else {
            // Store the user in the request attributes so that the actual handler can access the logged-in user
            request.setAttribute("user", user.get());

            //  Going to /style.css or /logout is always ok
            if (Set.of(
                    "/style.css",
                    "/markdown.css",
                    "/logout"
            ).contains(request.getServletPath())) {
                return HandlerInterceptor.super.preHandle(request, response, handler);
            }

            // Make sure the user is making a request to its respective section. i.e.:
            //   - /professor/* If the user is a professor
            //   - /student/* If the user is a student
            // If this is not the case, then redirect the user to its homepage. Note that this also forbids the user
            // from going to /login and /register. (But it allows going to /logout and /style.css because we've allowed
            // that before even reaching this line)
            User.Role pathRole;
            if (request.getServletPath().startsWith("/professor")) {
                pathRole = User.Role.PROFESSOR;
            } else if (request.getServletPath().startsWith("/student")) {
                pathRole = User.Role.STUDENT;
            } else {
                // This is not equal to User.Role.PROFESSOR nor User.Role.STUDENT so it can never be equal to
                // user.get().getRole() which means the user is redirected to its homepage. This happens i.e. when going
                // to /login or /register when already authenticated
                pathRole = null;
            }

            if (user.get().getRole() == pathRole) {
                return HandlerInterceptor.super.preHandle(request, response, handler);
            } else if (user.get().getRole() == User.Role.PROFESSOR) {
                response.sendRedirect("/professor/courses");
                return false;
            } else {
                response.sendRedirect("/student/courses");
                return false;
            }
        }
    }
}
