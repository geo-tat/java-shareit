package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User user;
    private User user2;

    private Item item;
    private Item item2;

    private Booking booking1;
    private Booking booking2;
    PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        pageRequest = PageRequest.of(0, 5);
        user = User.builder()
                .name("Harrison")
                .email("ford@test.com")
                .build();

        user2 = User.builder()
                .name("Tom")
                .email("cruise@test.com")
                .build();

        userRepository.save(user);
        userRepository.save(user2);

        item = Item.builder()
                .name("XBOX")
                .description("Microsoft gaming console")
                .available(true)
                .owner(user)
                .build();

        item2 = Item.builder()
                .name("PlayStation")
                .description("Sony gaming console")
                .available(true)
                .owner(user)
                .build();

        itemRepository.save(item);
        itemRepository.save(item2);

        booking1 = Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user2)
                .status(Status.APPROVED)
                .build();

        booking2 = Booking.builder()
                .start(LocalDateTime.now().plusHours(2))
                .end(LocalDateTime.now().plusHours(3))
                .item(item)
                .booker(user2)
                .status(Status.APPROVED)
                .build();

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

    }

    @Test
    void findNextBookingsForItems() {
        Collection<Integer> ids = List.of(1);
        List<Booking> bookingsNext = new ArrayList<>(bookingRepository.findNextBookingsForItems(
                ids,
                LocalDateTime.now(),
                Status.APPROVED));
            assertNotNull(bookingsNext);
            assertEquals(booking2,bookingsNext.get(0));
    }

    @Test
    void findLastBookingsForItems() {
        Collection<Integer> ids = List.of(1);
        List<Booking> bookingsLast = new ArrayList<>(bookingRepository.findLastBookingsForItems(
                ids,
                LocalDateTime.now(),
                Status.APPROVED));
        assertNotNull(bookingsLast);
        assertEquals(booking1,bookingsLast.get(0));
    }
}