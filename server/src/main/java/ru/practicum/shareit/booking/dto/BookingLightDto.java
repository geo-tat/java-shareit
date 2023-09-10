package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingLightDto {
    Integer id;
    LocalDateTime start;

    LocalDateTime end;
    Integer bookerId;
    Integer itemId;
}
