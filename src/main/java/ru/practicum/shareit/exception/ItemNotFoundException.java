package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class ItemNotFoundException extends RuntimeException {
    String message;

    public ItemNotFoundException(String message) {
        this.message = message;
    }

}
