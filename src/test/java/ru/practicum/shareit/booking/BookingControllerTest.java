package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.dto.BookingLightDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private Booking booking1;
    private Booking booking2;

    private BookingLightDto bookingLight1;
    private BookingDto bookingDto;
    private PageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        pageRequest = PageRequest.of(0, 5, Sort.by("start").descending());
        User user = User.builder()
                .id(1)
                .name("Harrison")
                .email("Ford@test.com")
                .build();

        User user2 = User.builder()
                .id(2)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        User user3 = User.builder()
                .id(3)
                .name("Jamie")
                .email("fox@test.com")
                .build();

        Item item = Item.builder()
                .id(1)
                .name("XBOX")
                .description("console from Microsoft")
                .available(true)
                .owner(user)
                .build();

        Item item2 = Item.builder()
                .id(2)
                .name("Play Station")
                .description("Gaming console")
                .available(true)
                .owner(user2)
                .build();

        booking1 = Booking.builder()
                .id(1)
                .start(LocalDateTime.now().plusMinutes(15))
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
        bookingDto = BookingMapper.toBookingDto(booking1);
        bookingLight1 = BookingLightDto.builder()
                .id(1)
                .bookerId(3)
                .itemId(1)
                .start(booking1.getStart())
                .end(booking1.getEnd())
                .build();

    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.add(any(BookingLightDto.class), anyInt())).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingLight1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Integer.class));

        verify(bookingService, times(1)).add(bookingLight1, 3);
    }

    @Test
    void updateBookingTest() throws Exception {
        when(bookingService.updateRequest(anyBoolean(), anyInt(), anyInt())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Integer.class));

        verify(bookingService, times(1)).updateRequest(true, 1, 1);
    }

    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getById(anyInt(), anyInt())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Integer.class));

        verify(bookingService, times(1)).getById(1, 1);
    }

    @Test
    void getAllByUserTest() throws Exception {
        when(bookingService.getAllByUser(anyInt(), anyString(), any(PageRequest.class))).thenAnswer(invocation -> {
            List<Booking> bookings = new ArrayList<>();
            bookings.add(booking1);
            bookings.add(booking2);
            return bookings;
        });
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(booking1, booking2))));

        verify(bookingService, times(1)).getAllByUser(1, "ALL", pageRequest);
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        when(bookingService.getAllByOwner(anyInt(), anyString(), any(PageRequest.class))).thenAnswer(invocation -> {
            List<Booking> bookings = new ArrayList<>();
            bookings.add(booking1);
            bookings.add(booking2);
            return bookings;
        });
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(booking1, booking2))));

        verify(bookingService, times(1)).getAllByOwner(1, "ALL", pageRequest);
    }

}