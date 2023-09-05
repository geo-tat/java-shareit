package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringJUnitConfig({ShareItApp.class, ItemServiceImpl.class, UserServiceImpl.class, BookingServiceImpl.class})
public class BookingServiceIntegrationTest {
    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final UserServiceImpl userService;
    private final BookingServiceImpl bookingService;

    private User owner;
    private User booker;

    private Item item;

    private Booking booking;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.of(2023, 12, 12, 12, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 12, 15, 0);
        owner = User.builder()
                .id(1)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        booker = User.builder()
                .id(2)
                .name("Tom")
                .email("holland@test.com")
                .build();

        item = Item.builder()
                .id(1)
                .name("XBOX")
                .description("Microsoft gaming console")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1)
                .start(start)
                .end(end)
                .booker(booker)
                .item(item)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void createBooking() {
        userService.create(UserMapper.toUserDto(owner));
        userService.create(UserMapper.toUserDto(booker));
        itemService.create(ItemMapper.toItemDto(item), 1);

        bookingService.add(BookingMapper.toBookingLightDto(booking), booker.getId());
        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking queryBooking = query
                .setParameter("id", 1)
                .getSingleResult();
        assertNotNull(queryBooking);
        assertEquals(Status.WAITING, queryBooking.getStatus());
        assertEquals(booking, queryBooking);
    }

    @Test
    void getById() {
        BookingDto result = bookingService.getById(1, 1);

        assertEquals(Status.WAITING, result.getStatus());
        assertEquals(booking.getEnd(), result.getEnd());
    }
}
