package it.ettore.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Error {
    private final String message;

    @Override
    public String toString() {
        return String.format("Error{%s}", message);
    }
}
