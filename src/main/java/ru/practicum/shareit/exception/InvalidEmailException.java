package ru.practicum.shareit.exception;

public class InvalidEmailException extends RuntimeException {
    String message;

    public InvalidEmailException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
