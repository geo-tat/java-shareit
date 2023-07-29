package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

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

    public Item update(Item item, int itemId, Item itemToUpdate) {
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

    public Item get(int id) {
        Item item = items.get(id);
        if (item == null) {
            throw new ItemNotFoundException("Вещь с id " + id + " не найдена");
        }
        return item;
    }

    public Collection<Item> getItems(int userId) {
        return items.values().stream().filter(item -> item.getOwner().getId() == userId).collect(Collectors.toList());
    }

    public boolean delete(int id) {
        boolean isRemoved = items.remove(id) != null;
        if (!isRemoved) {
            throw new ItemNotFoundException("Вещь с id " + id + " не найдена");
        }
        return isRemoved;
    }

    public Collection<Item> search(String text) {
        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(t -> t.getAvailable() && (t.getName().toLowerCase().contains(searchText) ||
                        t.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
    }


}

