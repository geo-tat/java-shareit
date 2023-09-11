package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;



import java.util.Collection;


@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {
    private final ItemServiceImpl service;


    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") int userId,
                          @RequestBody ItemDto item) {
        return service.create(item, userId);
    }

    @GetMapping
    public Collection<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                        @RequestParam(name = "from", defaultValue = "0") int from,
                                        @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return service.getItems(userId, pageRequest);
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
    public Collection<ItemDto> search(@RequestParam String text,
                                      @RequestParam(name = "from", defaultValue = "0") int from,
                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return service.search(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") int userId,
                                 @PathVariable int itemId,
                                 @RequestBody CommentDto comment) {
        return service.addComment(userId, itemId, comment);
    }
}
