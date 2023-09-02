package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto item, int userId);

    ItemDto update(ItemDto item, int userId, int itemId);

    Collection<ItemDto> getItems(int userId, PageRequest pageRequest);

    ItemDto get(int id, int userId);

    void delete(int id);

    Collection<ItemDto> search(String text, PageRequest pageRequest);

    CommentDto addComment(int userId, int itemId, CommentDto comment);
}
