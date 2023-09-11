package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class RequestController {
    private final RequestClient client;


    @PostMapping
    public ResponseEntity<Object> createRequest(@Validated
                                                @RequestBody RequestDto postRequestDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.createRequest(postRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                         Integer from,
                                         @Positive
                                         @RequestParam(name = "size", defaultValue = "10")
                                         Integer size,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@PathVariable Long requestId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getById(requestId, userId);
    }
}
