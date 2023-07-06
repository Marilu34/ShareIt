package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemDbStorage {
    Item createItem(Item item, User user);

    Item updateItem(Long itemId, User user, Item item);

    Item getItemByItemId(Long itemId);

    List<Item> getPersonalItems(Long userId);

    List<Item> getFoundItems(String text);
}
