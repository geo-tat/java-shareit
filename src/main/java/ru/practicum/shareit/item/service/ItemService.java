package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto item, int userId);

    ItemDto update(ItemDto item, int userId, int itemId);

    Collection<ItemDto> getItems(int userId);

    ItemDto get(int id, int userId);

    void delete(int id);

    Collection<ItemDto> search(String text);

    CommentDto addComment(int userId, int itemId, CommentDto comment);
}
