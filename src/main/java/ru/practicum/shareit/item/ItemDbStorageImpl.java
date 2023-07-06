package ru.practicum.shareit.item;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemDbStorageImpl implements ItemDbStorage {
    private final Map<Long, List<Item>> items;
    private Long id;

    public ItemDbStorageImpl() {
        this.items = new HashMap<>();
        this.id = 1L;
    }

    @Override
    public Item createItem(Item item, User user) {
        item.setId(generatedId());
        item.setOwner(user);
        items.compute(user.getId(), (k, v) -> {
            if (v == null) {
                v = new ArrayList<>();
            }
            v.add(item);
            return v;
        });
        log.info("Вещь {} добавлена пользователем {}", item, user);
        return item;
    }

    @Override
    public Item updateItem(Long itemId, User user, Item item) {
        checkOwner(itemId, user);
        Item oldItem = items.get(user.getId()).stream().filter(i -> i.getId().equals(itemId))
                .findFirst().orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Предмета с id=" + itemId + " нет"));
        item.setOwner(user);
        item.setId(itemId);
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        items.get(user.getId()).remove(oldItem);
        items.get(user.getId()).add(item);
        log.info("Пользователь {} обновил вещь {}", user, item);
        return item;
    }

    @Override
    public Item getItemByItemId(Long itemId) {
        return items.values().stream()
                .filter(list -> list.stream().anyMatch(i -> i.getId().equals(itemId)))
                .map(list -> list.stream().filter(i -> i.getId().equals(itemId)).findFirst().orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Предмета с id=" + itemId + " нет")))
                .findFirst().get();
    }

    @Override
    public List<Item> getPersonalItems(Long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public List<Item> getFoundItems(String text) {
        String search = text.toLowerCase();
        List<Item> result = new ArrayList<>();
        items.values().forEach(itemsList -> result.addAll(
                itemsList.stream().filter(item -> (item.getDescription().toLowerCase().contains(search)
                                || item.getName().toLowerCase().contains(search)) && item.getAvailable())
                        .collect(Collectors.toList())));
        return result;
    }


    private Long generatedId() {
        return id++;
    }

    private void checkOwner(Long itemId, User user) {
        if (items.get(user.getId()) == null || items.get(user.getId()).stream().noneMatch(i -> i.getId().equals(itemId))) {
            log.info("Вещь с id={} не пренадлежит пользователю с id={}", itemId, user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Вещь с id=" + itemId + " не пренадлежит пользователю с id=" + user.getId());
        }
    }
}

