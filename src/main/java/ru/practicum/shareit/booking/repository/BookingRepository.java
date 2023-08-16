package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.BookingLightDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {


    Collection<Booking> findAllBookingsByBookerId(int userId, Sort sort);

    Collection<Booking> findAllBookingsByBookerIdAndStartBeforeAndEndAfter(int userId, LocalDateTime start, LocalDateTime end, Sort sort);

    Collection<Booking> findAllBookingsByBookerIdAndEndBefore(int userId, LocalDateTime now, Sort sort);

    Collection<Booking> findAllBookingsByBookerIdAndStartAfter(int userId, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByItemOwner(User user, Sort sort);

    Collection<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User user, LocalDateTime now, LocalDateTime now1, Sort sort);

    Collection<Booking> findAllByItemOwnerAndEndBefore(User user, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByItemOwnerAndStartAfter(User user, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByItemOwnerAndStatusEquals(User user, Status status, Sort sort);

    Collection<Booking> findAllByBookerIdAndStatusEquals(int userId, Status status, Sort sort);

    BookingLightDto findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(int id, LocalDateTime now, Status status);

    BookingLightDto findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(int id, LocalDateTime now, Status status);

    Collection<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(int userId, int itemId, Status status, LocalDateTime now);

    BookingLightDto findFirstByItemAndStartAfterAndStatusOrderByStartAsc(Item item, LocalDateTime now, Status status);

    BookingLightDto findFirstByItemAndStartBeforeAndStatusOrderByEndDesc(Item item, LocalDateTime now, Status status);
}
