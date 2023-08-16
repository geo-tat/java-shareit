package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingLightDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDto add(BookingLightDto booking, int userId) {
        Booking bookingToSave = BookingMapper.toBooking(booking);
        User user = userRepo.findById(userId).stream()
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + userId + " не найден."));
        Item item = itemRepo.findById(booking.getItemId())
                .stream()
                .findAny()
                .orElseThrow(() -> new ItemNotFoundException("Предмет c ID=" + booking.getItemId() + " не найден."));
        if (!item.getAvailable()) {
            throw new AvailableException("Предмет недоступен для аренды");
        }
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().equals(booking.getEnd())) {
            throw new WrongTimeException("Время течет в другую сторону.");
        }
        if (item.getOwner().getId() == userId) {
            throw new ItemNotFoundException("Вы не можете забронировать свой предмет.");
        }
        bookingToSave.setBooker(user);
        bookingToSave.setItem(item);
        bookingToSave.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(repository.save(bookingToSave));
    }

    @Override
    @Transactional
    public BookingDto updateRequest(boolean isApproved, int bookingId, int userId) {
        User user = userRepo.findById(userId).stream()
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + userId + " не найден."));
        Booking booking = repository.findById(bookingId).stream()
                .findAny()
                .orElseThrow(() -> new BookingNotFoundException("Заказ с Id=" + bookingId + " не найден."));
        if (!booking.getItem().getOwner().equals(user)) {
            throw new NotYourItemException("Пользователь запросил доступ к чужому предмету.");
        }
        if (isApproved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new WrongStateException("Статус уже установлен");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        repository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(int bookingId, int userId) {
        User user = userRepo.findById(userId).stream()
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + userId + " не найден."));
        Booking booking = repository.findById(bookingId).stream()
                .findAny()
                .orElseThrow(() -> new BookingNotFoundException("Заказ с Id=" + bookingId + " не найден."));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Доступ имеет только владелец или арендатор предмета.");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> getAllByOwner(Integer ownerId, String state) {
        User user = userRepo.findById(ownerId).stream()
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + ownerId + " не найден."));

        Collection<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = repository.findAllByItemOwner(user, sort);
                break;
            case "CURRENT":
                bookings = repository.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(), LocalDateTime.now(), sort);
                break;
            case "PAST":
                bookings = repository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = repository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookings = repository.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case "REJECTED":
                bookings = repository.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                throw new WrongStateException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> getAllByUser(int userId, String state) {

        User user = userRepo.findById(userId).stream()
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("Пользователь c ID=" + userId + " не найден."));
        Collection<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = repository.findAllBookingsByBookerId(userId, sort);
                break;
            case "CURRENT":
                bookings = repository.findAllBookingsByBookerIdAndStartBeforeAndEndAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sort);
                break;
            case "PAST":
                bookings = repository.findAllBookingsByBookerIdAndEndBefore(userId, LocalDateTime.now(), sort);
                break;
            case "FUTURE":
                bookings = repository.findAllBookingsByBookerIdAndStartAfter(userId, LocalDateTime.now(), sort);
                break;
            case "WAITING":
                bookings = repository.findAllByBookerIdAndStatusEquals(userId, Status.WAITING, sort);
                break;
            case "REJECTED":
                bookings = repository.findAllByBookerIdAndStatusEquals(userId, Status.REJECTED, sort);
                break;
            default:
                throw new WrongStateException("Unknown state: " + state);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
