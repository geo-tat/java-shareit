package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") int userId,
                       @Valid @RequestBody ItemRequestDto body) {
        return service.addRequest(userId, body);
    }

    @GetMapping
    Collection<ItemRequestFullDto> getOwnRequests(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        return service.getOwnRequests(ownerId);
    }

    @GetMapping("/all")
    Collection<ItemRequestFullDto> getAllRequests(@PositiveOrZero
                                                  @RequestParam(name = "from", defaultValue = "0") int from,
                                                  @Positive
                                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                                  @RequestHeader("X-Sharer-User-Id") int userId) {
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by("created").descending());
        return service.getAllRequests(userId, pageRequest);
    }

    @GetMapping("/{requestId}")
    ItemRequestFullDto getRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
                                      @PathVariable int requestId) {
        return service.getById(requestId, userId);
    }
}
