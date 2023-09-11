package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    String message;

    public UserNotFoundException(String message) {
        this.message = message;
    }

}
