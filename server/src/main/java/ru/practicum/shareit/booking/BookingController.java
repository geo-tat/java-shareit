package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingLightDto;
import ru.practicum.shareit.booking.service.BookingService;



import java.util.Collection;


@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") int userId,
                              @RequestBody BookingLightDto booking) {
        return service.add(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateApprove(@RequestHeader("X-Sharer-User-Id") int userId,
                                    @PathVariable int bookingId, @RequestParam boolean approved) {
        return service.updateRequest(approved, bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") int userId,
                              @PathVariable int bookingId) {
        return service.getById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") int userId,
                                               @RequestParam(name = "state", defaultValue = "ALL") String state,
                                               //     @PositiveOrZero(message = "Ошибка параметра 'from'!")
                                               @RequestParam(name = "from", defaultValue = "0") int from,
                                            //   @Positive(message = "Ошибка параметра 'size'!")
                                               @RequestParam(name = "size", defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("start").descending());
        return service.getAllByUser(userId, state, pageRequest);
    }

    @GetMapping("/owner")
    @Validated
    public Collection<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam(name = "state", defaultValue = "ALL") String state,
                                              //  @PositiveOrZero(message = "Ошибка параметра 'from'!")
                                                @RequestParam(name = "from", defaultValue = "0") int from,
                                              //  @Positive(message = "Ошибка параметра 'size'!")
                                                @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("start").descending());
        return service.getAllByOwner(userId, state, pageRequest);
    }
}
