package ru.practicum.shareit.exception;

public class UserNotFoundException extends RuntimeException {
    String message;

    public UserNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
