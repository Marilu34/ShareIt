package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(Item item);

    Item deleteItem(Long userId);

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> searchItemByQuery(String text);

    void deleteItemsByOwner(Long ownderId);

    Item getItemById(Long itemId);
}

