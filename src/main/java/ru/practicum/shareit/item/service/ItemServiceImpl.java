package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotYourItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepo;

    @Override
    public ItemDto create(ItemDto itemDto, int userId) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepo.get(userId);
        item.setOwner(user);
        return ItemMapper.toItemDto(repository.create(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, int userId, int itemId) {
        Item item = ItemMapper.toItem(itemDto);
        User user = userRepo.get(userId);
        Item itemToUpdate = repository.get(itemId);
        if (itemToUpdate.getOwner().getId() != user.getId()) {
            throw new NotYourItemException("Вы не являетесь владельцем данного предмета.");
        }
        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return ItemMapper.toItemDto(repository.update(itemId, itemToUpdate));
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
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return repository.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
