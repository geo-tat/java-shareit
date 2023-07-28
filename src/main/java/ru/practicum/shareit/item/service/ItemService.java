package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Item item, int userId);

    ItemDto update(Item item, int userId, int itemId);

    Collection<ItemDto> getItems(int userId);

    ItemDto get(int id);

    boolean delete(int id);

    Collection<ItemDto> search(String text);
}
