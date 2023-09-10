package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BookingLightDto {
    Long id;
    @FutureOrPresent(groups = {Create.class})
    @NotNull
    LocalDateTime start;
    @Future(groups = {Create.class})
    @NotNull
    LocalDateTime end;
    Long bookerId;
    Long itemId;
}
