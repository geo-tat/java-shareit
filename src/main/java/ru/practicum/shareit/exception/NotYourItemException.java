package ru.practicum.shareit.exception;

public class NotYourItemException extends RuntimeException {
    String message;

    public NotYourItemException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
