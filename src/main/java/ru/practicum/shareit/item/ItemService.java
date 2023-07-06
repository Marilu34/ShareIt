package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long ownerId);
    List<ItemDto> getItemsByOwner(Long ownderId);
    ItemDto getItemById(Long id);
    ItemDto update(ItemDto itemDto, Long ownerId, Long itemId);
    ItemDto delete(Long itemId, Long ownerId);
    void deleteItemsByOwner(Long ownderId);
    List<ItemDto> getItemsBySearchQuery(String text);

}
