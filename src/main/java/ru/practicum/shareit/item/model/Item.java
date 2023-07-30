package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Builder
@Data
public class Item {
    int id;
    String name;
    String description;
    Boolean available;
    User owner;
    ItemRequest request;
}

