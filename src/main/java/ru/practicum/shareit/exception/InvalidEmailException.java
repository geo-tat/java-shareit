package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class InvalidEmailException extends RuntimeException {
    String message;

    public InvalidEmailException(String message) {
        this.message = message;
    }

}
