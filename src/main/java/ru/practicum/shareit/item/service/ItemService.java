package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto item, int userId);

    ItemDto update(ItemDto item, int userId, int itemId);

    Collection<ItemDto> getItems(int userId);

    ItemDto get(int id);

    boolean delete(int id);

    Collection<ItemDto> search(String text);
}
