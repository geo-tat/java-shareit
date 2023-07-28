package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotYourItemException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    int idCounter = 0;

    public Item create(Item item) {
        idCounter++;
        item.setId(idCounter);
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item item, int userId, int itemId) {

        if (items.containsKey(itemId)) {
            Item itemToUpdate = items.get(itemId);
            if (itemToUpdate.getOwnerId() != userId) {
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
            items.put(itemId, itemToUpdate);
            return itemToUpdate;
        }
        throw new ItemNotFoundException("Вещь с id " + item.getId() + " не найдена");
    }

    public Item get(int id) {
        if (items.containsKey(id)) {
            return items.get(id);
        }
        throw new ItemNotFoundException("Вещь с id " + id + " не найдена");
    }

    public Collection<Item> getItems(int userId) {
        return items.values().stream().filter(item -> item.getOwnerId() == userId).collect(Collectors.toList());
    }

    public boolean delete(int id) {
        if (items.containsKey(id)) {
            items.remove(id);
            return true;
        }
        throw new ItemNotFoundException("Вещь с id " + id + " не найдена");
    }

    public Collection<Item> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(t -> t.getName().toLowerCase().contains(text.toLowerCase()) ||
                        t.getDescription().toLowerCase().contains(text.toLowerCase()) && t.getAvailable())
                .collect(Collectors.toList());
    }


}

