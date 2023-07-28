package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Booking {
    int id;
    LocalDateTime start;
    LocalDateTime end;
    int itemId;
    int booker; // userId
    Status status;
}
