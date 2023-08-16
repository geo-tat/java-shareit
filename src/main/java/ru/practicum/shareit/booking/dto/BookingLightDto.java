package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingLightDto {
    int id;
    @Future
    @NotNull
    LocalDateTime start;
    @Future
    @NotNull
    LocalDateTime end;
    Integer bookerId;
    Integer itemId;
}
