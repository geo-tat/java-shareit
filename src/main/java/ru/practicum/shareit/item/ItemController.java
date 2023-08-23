package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;


import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemServiceImpl service;


    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") int userId,
                          @Valid @RequestBody ItemDto item) {
        return service.create(item, userId);
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return service.getItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto get(@PathVariable int id,
                       @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.get(id, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id,
                          @RequestBody ItemDto item) {
        return service.update(item, userId, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return service.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                 @PathVariable int itemId,
                                 @RequestBody @Valid CommentDto comment) {
        return service.addComment(userId, itemId, comment);
    }
}
