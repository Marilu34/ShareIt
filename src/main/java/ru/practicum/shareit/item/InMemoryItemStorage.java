package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {

    public HashMap<Long, Item> items;
    private Long idItem = 0L;

    public InMemoryItemStorage() {
        items = new HashMap<>();
    }

    @Override
    public Item createItem(Item item) {
        if (ifItemValid(item)) {
            item.setId(++idItem);
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        if (item.getId() == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        if (!items.containsKey(item.getId())) {
            throw new ItemNotFoundException("Вещь с ID=" + item.getId() + " не найдена!");
        }
        if (item.getName() == null) {
            item.setName(items.get(item.getId()).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(item.getId()).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(item.getId()).getAvailable());
        }
        if (ifItemValid(item)) {
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Item delete(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Передан пустой аргумент!");
        }
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Вещь с ID=" + itemId + " не найден!");
        }
        return items.remove(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        return new ArrayList<>(items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(toList()));
    }

    @Override
    public void deleteItemsByOwner(Long ownerId) {
        List<Long> deleteIds = new ArrayList<>(items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .map(item -> item.getId())
                .collect(toList()));
        for (Long deleteId : deleteIds) {
            items.remove(deleteId);
        }
    }

    public void deleteItemsByUser(Long userId) {
        deleteItemsByOwner(userId);
    }

    @Override
    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Вещь с ID=" + itemId + " не найдена!");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> searchItemByQuery(String text) {
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            searchItems = items.values().stream()
                    .filter(item -> item.getAvailable())
                    .filter(item -> item.getName().toLowerCase().contains(text) ||
                            item.getDescription().toLowerCase().contains(text))
                    .collect(toList());
        }
        return searchItems;
    }

    private boolean ifItemValid(Item item) {
        if ((item.getName().isEmpty()) || (item.getDescription().isEmpty()) || (item.getAvailable() == null)) {
            throw new ValidationException("У вещи некорректные данные");
        }
        return true;
    }
}
