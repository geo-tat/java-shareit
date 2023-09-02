package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemBookerDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto result = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getRequest() != null) {
            result.setRequestId(item.getRequest().getId());
        }
        return result;
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        ItemForRequestDto result = ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getRequest() != null) {
            result.setRequestId(item.getRequest().getId());
        }

        return result;
    }

    public static ItemBookerDto toItemBookerDto(ItemDto itemDto) {
        return ItemBookerDto.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .build();
    }
}
