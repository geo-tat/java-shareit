package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingLightDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private BookingRepository bookingRepository;

    private User user;
    private User user2;
    private User user3;
    private Item item;
    private ItemDto itemDto;
    private Booking booking1;
    private Booking booking2;

    private BookingLightDto bookingLight1;
    private PageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        pageRequest = PageRequest.of(0, 5);
        user = User.builder()
                .id(1)
                .name("Harrison")
                .email("Ford@test.com")
                .build();

        user2 = User.builder()
                .id(2)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        user3 = User.builder()
                .id(3)
                .name("Jamie")
                .email("fox@test.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1)
                .description("Nintendo")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1)
                .name("XBOX")
                .description("console from Microsoft")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        Item item2 = Item.builder()
                .id(2)
                .name("Play Station")
                .description("Gaming console")
                .available(true)
                .owner(user2)
                .build();


        itemDto = ItemMapper.toItemDto(item);
        ItemDto itemDto2 = ItemMapper.toItemDto(item2);

        Comment comment = Comment.builder()
                .id(1)
                .author(user)
                .created(LocalDateTime.now())
                .text("Don't work")
                .build();
        booking1 = Booking.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(30))
                .item(item)
                .booker(user3)
                .status(Status.WAITING)
                .build();

        booking2 = Booking.builder()
                .id(2)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user3)
                .status(Status.APPROVED)
                .build();

        bookingLight1 = BookingMapper.toBookingLightDto(booking1);
    }

    @Test
    void createBookingTest() {
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);

        BookingDto bookingOutDtoTest = bookingService.add(bookingLight1, user3.getId());

        assertEquals(bookingOutDtoTest.getItem(), ItemMapper.toItemBookerDto(itemDto));
        assertEquals(bookingOutDtoTest.getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.toUserBooker(user3));

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> bookingService.add(bookingLight1, 44));
    }

    @Test
    void createBookingItemNotFound() {
        bookingLight1.setItemId(55);
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(ItemNotFoundException.class, () -> bookingService.add(bookingLight1, 1));

    }

    @Test
    void createBookingOwnItem() {
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(ItemNotFoundException.class, () -> bookingService.add(bookingLight1, user.getId()));
    }

    @Test
    void createBookingItemBooked() {

        item.setAvailable(false);

        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));

        assertThrows(AvailableException.class, () -> bookingService.add(bookingLight1, anyInt()));
    }
    /*
    @Test
    void createBookingNotValidEnd() {
        when(itemRepository.existsById(anyInt())).thenReturn(true);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));

        bookingLight1.setEnd(LocalDateTime.of(2020, 4, 7, 0, 0));

        assertThrows(WrongTimeException.class, () -> bookingService.add(bookingLight1, user2.getId()));
    }
*/

    @Test
    void updateBookingTest() {
        BookingDto test;

        when(bookingRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        test = bookingService.updateRequest(true, booking1.getId(), user.getId());
        assertEquals(test.getStatus(), Status.APPROVED);

        test = bookingService.updateRequest(false, booking1.getId(), user.getId());
        assertEquals(test.getStatus(), Status.REJECTED);


        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    void updateBookingWrongUser() {
        when(bookingRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking2);
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        assertThrows(NotYourItemException.class, () -> bookingService.updateRequest(true, booking2.getId(), user2.getId()));
    }

    @Test
    void updateBookingUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> bookingService.updateRequest(true, booking2.getId(), 44));
    }

    @Test
    void updateBookingWhenBookingNotFound() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        assertThrows(BookingNotFoundException.class, () -> bookingService.updateRequest(true, 55, 1));
    }

    @Test
    void updateBookingWrongStatus() {
        when(bookingRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking2);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        assertThrows(WrongStateException.class, () -> bookingService.updateRequest(true, booking2.getId(), user.getId()));
    }

    @Test
    void getById() {
        when(bookingRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking1));
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        BookingDto bookingOutDtoTest = bookingService.getById(booking1.getId(), user.getId());

        assertEquals(bookingOutDtoTest.getItem(), ItemMapper.toItemBookerDto(itemDto));
        assertEquals(bookingOutDtoTest.getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.toUserBooker(user3));

    }

    @Test
    void getByIdError() {
        when(bookingRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking1));
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));

        assertThrows(NotFoundException.class, () -> bookingService.getById(2, user2.getId()));
    }

    @Test
    void getByIdNotFound() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user2));
        assertThrows(BookingNotFoundException.class, () -> bookingService.getById(4, user2.getId()));
    }


    @Test
    void getAllByUserTest() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user3));

        when(bookingRepository.findAllBookingsByBookerId(anyInt(), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        String state = "ALL";

        List<BookingDto> test = new ArrayList<>(bookingService.getAllByUser(user3.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));

        when(bookingRepository.findAllBookingsByBookerIdAndStartBeforeAndEndAfter(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        state = "CURRENT";

        test = new ArrayList<>(bookingService.getAllByUser(user3.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));

        when(bookingRepository.findAllBookingsByBookerIdAndEndBefore(anyInt(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        state = "PAST";

        test = new ArrayList<>(bookingService.getAllByUser(user3.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));

        when(bookingRepository.findAllBookingsByBookerIdAndStartAfter(anyInt(), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        state = "FUTURE";

        test = new ArrayList<>(bookingService.getAllByUser(user3.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));
        ;

        when(bookingRepository.findAllByBookerIdAndStatusEquals(anyInt(), any(Status.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        state = "WAITING";

        test = new ArrayList<>(bookingService.getAllByUser(user3.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));

        when(bookingRepository.findAllByBookerIdAndStatusEquals(anyInt(), any(Status.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        state = "REJECTED";

        test = new ArrayList<>(bookingService.getAllByUser(user3.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));
    }


    @Test
    void getAllByOwnerTest() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemOwner(any(User.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1)));

        String state = "ALL";

        List<BookingDto> test = new ArrayList<>(bookingService.getAllByOwner(user.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));

        when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1)));
        state = "CURRENT";

        test = new ArrayList<>(bookingService.getAllByOwner(user.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));

        when(bookingRepository.findAllByItemOwnerAndEndBefore(any(User.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1)));
        state = "PAST";

        test = new ArrayList<>(bookingService.getAllByOwner(user.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));

        when(bookingRepository.findAllByItemOwnerAndStartAfter(any(User.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1)));
        state = "FUTURE";

        test = new ArrayList<>(bookingService.getAllByOwner(user.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));

        when(bookingRepository.findAllByItemOwnerAndStatusEquals(any(User.class), any(Status.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1)));
        state = "WAITING";

        test = new ArrayList<>(bookingService.getAllByOwner(user.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));

        when(bookingRepository.findAllByItemOwnerAndStatusEquals(any(User.class), any(Status.class), any(PageRequest.class))).thenReturn(new ArrayList<>(List.of(booking1)));
        state = "REJECTED";

        test = new ArrayList<>(bookingService.getAllByOwner(user.getId(), state, pageRequest));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserBooker(user3));
    }

    @Test
    void getAllBookingsForAllItemsByOwnerIdNotHaveItems() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findAllItemsByOwnerId(anyInt(), any(PageRequest.class))).thenReturn(List.of());

        assertEquals(new ArrayList<>(), bookingService.getAllByOwner(user.getId(), "ALL", pageRequest));
    }
}