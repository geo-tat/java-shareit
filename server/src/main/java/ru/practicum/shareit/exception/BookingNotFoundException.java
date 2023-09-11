package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class BookingNotFoundException extends RuntimeException {
    String message;

    public BookingNotFoundException(String message) {
        this.message = message;
    }

}
