package it.ettore.utils;

public class Utils {
    /**
     * Returns true if the provided exception, or any of its recursively retrieved causes, is of the provided target
     * class.
     * @param hay The exception
     * @param needle The target class that we want to search among the causes of the exception
     */
    public static boolean IsCause(Throwable hay, Class<?> needle) {
        if (hay == null) return false;
        if (needle.isInstance(hay)) return true;
        return IsCause(hay.getCause(), needle);
    }
}
