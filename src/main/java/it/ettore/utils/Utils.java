package it.ettore.utils;

import it.ettore.model.User;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class Utils {
    /**
     * Returns true if the provided exception, or any of its recursively retrieved causes, is of the provided target
     * class.
     *
     * @param hay    The exception
     * @param needle The target class that we want to search among the causes of the exception
     */
    public static boolean IsCause(Throwable hay, Class<?> needle) {
        if (hay == null) return false;
        if (needle.isInstance(hay)) return true;
        return IsCause(hay.getCause(), needle);
    }

    /**
     * Helper function that automatically retrieves the logged-in user from the request's attributes in an authenticated
     * endpoint.
     *
     * @param request The ongoing request
     * @return The logged-in use
     */
    public static User loggedUser(HttpServletRequest request) {
        Object userObj = request.getAttribute("user");
        if (!(userObj instanceof User)) throw new IllegalStateException("expected user");
        return (User) userObj;
    }

    public static void addError(Model model, String message) {
        if (!model.containsAttribute("errors")) {
            model.addAttribute("errors", new ArrayList<Error>());
        }
        ((ArrayList<Error>) model.getAttribute("errors")).add(new Error(message));
    }
}
