package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto addRequest(int userId, ItemRequestDto body);

    Collection<ItemRequestFullDto> getOwnRequests(int ownerId);

    Collection<ItemRequestFullDto> getAllRequests(int userId, PageRequest pageRequest);

    ItemRequestFullDto getById(int requestId, int userId);
}
