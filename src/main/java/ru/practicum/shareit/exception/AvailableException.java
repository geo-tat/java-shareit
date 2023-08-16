package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class AvailableException extends RuntimeException {
    String message;

    public AvailableException(String message) {
        this.message = message;
    }
}
