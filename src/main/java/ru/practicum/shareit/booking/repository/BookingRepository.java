package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {


    Collection<Booking> findAllBookingsByBookerId(int userId, Sort sort);

    Collection<Booking> findAllBookingsByBookerIdAndStartBeforeAndEndAfter(int userId,
                                                                           LocalDateTime start,
                                                                           LocalDateTime end,
                                                                           Sort sort);

    Collection<Booking> findAllBookingsByBookerIdAndEndBefore(int userId, LocalDateTime now, Sort sort);

    Collection<Booking> findAllBookingsByBookerIdAndStartAfter(int userId, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByItemOwner(User user, Sort sort);

    Collection<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User user,
                                                                    LocalDateTime now,
                                                                    LocalDateTime now1,
                                                                    Sort sort);

    Collection<Booking> findAllByItemOwnerAndEndBefore(User user, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByItemOwnerAndStartAfter(User user, LocalDateTime now, Sort sort);

    Collection<Booking> findAllByItemOwnerAndStatusEquals(User user, Status status, Sort sort);

    Collection<Booking> findAllByBookerIdAndStatusEquals(int userId, Status status, Sort sort);


    Collection<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(int userId,
                                                                                int itemId,
                                                                                Status status,
                                                                                LocalDateTime now);

    Booking findFirstByItemAndStartAfterAndStatusOrderByStartAsc(Item item, LocalDateTime now, Status status);

    Booking findFirstByItemAndStartBeforeAndStatusOrderByEndDesc(Item item, LocalDateTime now, Status status);

    Collection<Booking> findAllByItemIdAndStartAfterAndEndBefore(int id, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.start > :now " +
            "AND b.status = :status " +
            "ORDER BY b.start ASC")
    Collection<Booking> findNextBookingsForItems(Collection<Integer> itemIds, LocalDateTime now, Status status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id IN :itemIds " +
            "AND b.end < :now " +
            "AND b.status = :status " +
            "ORDER BY b.end DESC")
    Collection<Booking> findLastBookingsForItems(@Param("itemIds") Collection<Integer> itemIds,
                                                 @Param("now") LocalDateTime now,
                                                 @Param("status") Status status);
}