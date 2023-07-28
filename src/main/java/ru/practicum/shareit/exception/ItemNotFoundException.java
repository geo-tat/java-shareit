package ru.practicum.shareit.exception;

public class ItemNotFoundException extends RuntimeException {
    String message;

    public ItemNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
