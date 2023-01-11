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
            "/login"
        );

        if (whitelist.contains(request.getServletPath())) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        Object pswHashObj = request.getSession().getAttribute("PSW_HASH");
        if (!(pswHashObj instanceof String)) {
            response.sendRedirect("/login");
            return false;
        }
        String pswHash = (String) pswHashObj;

        Optional<User> user = repoUser.findByPswHash(pswHash);
        if (user.isEmpty()) {
            response.sendRedirect("/login");
            return false;
        }

        request.setAttribute("user", user.get());

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
