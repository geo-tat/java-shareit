package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    String message;

    public NotFoundException(String message) {
        this.message = message;
    }
}
