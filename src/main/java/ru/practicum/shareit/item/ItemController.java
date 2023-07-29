package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public ItemDto get(@PathVariable int id) {
        return service.get(id);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int id,
                          @RequestBody ItemDto item) {
        return service.update(item, userId, id);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable int id) {
        return service.delete(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text) {
        return service.search(text);
    }
}
