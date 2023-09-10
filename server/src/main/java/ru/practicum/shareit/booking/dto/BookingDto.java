package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemBookerDto;
import ru.practicum.shareit.user.dto.UserBookerDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    Integer id;
    LocalDateTime start;
    LocalDateTime end;
    UserBookerDto booker;
    ItemBookerDto item;
    Status status;
}
