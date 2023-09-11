package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NotYourItemException extends RuntimeException {
    String message;

    public NotYourItemException(String message) {
        this.message = message;
    }

}
