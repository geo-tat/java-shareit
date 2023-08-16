package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class WrongStateException extends RuntimeException {
    String error;

    public WrongStateException(String error) {
        this.error = error;
    }
}
