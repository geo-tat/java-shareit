package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class WrongTimeException extends RuntimeException {
    String message;

    public WrongTimeException(String message) {
        this.message = message;
    }

}
