package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepo;

    @Override
    public ItemDto create(Item item, int userId) {
        User user = userRepo.get(userId);
        item.setOwnerId(userId);
        return ItemMapper.toItemDto(repository.create(item));
    }

    @Override
    public ItemDto update(Item item, int userId, int itemId) {
        User user = userRepo.get(userId);
        return ItemMapper.toItemDto(repository.update(item, userId, itemId));
    }

    @Override
    public Collection<ItemDto> getItems(int userId) {
        return repository.getItems(userId).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto get(int id) {
        return ItemMapper.toItemDto(repository.get(id));
    }

    @Override
    public boolean delete(int id) {
        return repository.delete(id);
    }

    @Override
    public Collection<ItemDto> search(String text) {
        return repository.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private void validation(Item item) {

    }
}
