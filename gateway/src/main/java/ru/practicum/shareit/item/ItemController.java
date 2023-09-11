package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated(Create.class)
                                             @RequestBody ItemDto itemDto,
                                             @NotNull(message = "Должен быть указан Id владельца")
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return client.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId) {
        return client.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive
            @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        return client.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestParam(value = "text", required = false) String text,
                                            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                            Integer from,
                                            @Positive
                                            @RequestParam(name = "size", defaultValue = "10")
                                            Integer size) {
        return client.search(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@Valid @RequestBody CommentDto commentDto,
                                              @RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId) {

        return client.createComment(commentDto, itemId, userId);
    }
}
