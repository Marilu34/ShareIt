package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            throw new ValidationException("id не может быть пустым");
        }
        if (!items.containsKey(item.getId())) {
            throw new ItemNotFoundException("Вещь с id = " + item.getId() + " не обнаружена!");
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
    public Item deleteItem(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("id не может быть пустым");
        }
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Вещь с id = " + itemId + " не обнаружена!");
        }
        return items.remove(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        List<Item> itemsByOwner = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId().equals(ownerId)) {
                itemsByOwner.add(item);
            }
        }
        return itemsByOwner;
    }

    @Override
    public void deleteItemsByOwner(Long ownerId) {
        List<Long> deleteIds = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwnerId().equals(ownerId)) {
                deleteIds.add(item.getId());
            }
        }
        for (Long deleteId : deleteIds) {
            items.remove(deleteId);
        }
    }


    @Override
    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Вещь с id = " + itemId + " не обнаружена!");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> searchItemByQuery(String text) {
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            for (Item item : items.values()) {
                if (item.getAvailable() &&
                        (item.getName().toLowerCase().contains(text) ||
                                item.getDescription().toLowerCase().contains(text))) {
                    searchItems.add(item);
                }
            }
        }
        return searchItems;
    }


    private boolean ifItemValid(Item item) {
        if ((item.getName().isEmpty()) || (item.getDescription().isEmpty()) || (item.getAvailable() == null)) {
            throw new ValidationException("Ошибка! Вещь имеет некорректные данные");
        }
        return true;
    }

}
