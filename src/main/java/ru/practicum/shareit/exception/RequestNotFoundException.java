package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class RequestNotFoundException extends RuntimeException {
    String message;

    public RequestNotFoundException(String message) {
        this.message = message;
    }
}
