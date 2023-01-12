package it.ettore;

import it.ettore.model.User;
import it.ettore.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * An interceptor that allows all whitelisted requests to pass-through, while all other requests need to be
 * authenticated. The logged-in user object is then added to the request parameters so that the actual handler can
 * access it.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private UserRepository repoUser;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // These do not require any authentication
        List<String> whitelist = List.of(
                "/style.css",
                "/register",
                "/login",
                "/logout"
        );

        // If requested URL is whitelisted, we don't need to make any authentication check
        if (whitelist.contains(request.getServletPath())) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        // Else, get the password hash from the session. If it is null (or not a string, which would be weird) then
        // redirect the user to the login page
        Object pswHashObj = request.getSession().getAttribute("PSW_HASH");
        if (!(pswHashObj instanceof String)) {
            response.sendRedirect("/login");
            return false;
        }
        String pswHash = (String) pswHashObj;

        // Find the user with the matching password hash. If none exists, redirect the user to the login page
        Optional<User> user = repoUser.findByPswHash(pswHash);
        if (user.isEmpty()) {
            response.sendRedirect("/login");
            return false;
        }

        // Store the user in the request attributes so that the actual handler can access the logged-in user
        request.setAttribute("user", user.get());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}