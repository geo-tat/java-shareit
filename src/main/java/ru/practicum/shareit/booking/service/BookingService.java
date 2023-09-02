package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingLightDto;

import java.util.Collection;

public interface BookingService {
    BookingDto add(BookingLightDto booking, int userId);

    BookingDto updateRequest(boolean isApproved, int bookingId, int userId);

    BookingDto getById(int bookingId, int userId);

    Collection<BookingDto> getAllByOwner(Integer userId, String state, PageRequest pageRequest);

    Collection<BookingDto> getAllByUser(int userId, String state, PageRequest pageRequest);

}
