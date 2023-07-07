package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long ownerId);

    List<ItemDto> getOwnersItems(Long ownderId);

    ItemDto getItemById(Long id);

    ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId);

    ItemDto deleteItem(Long itemId, Long ownerId);

    void deleteItemsByOwner(Long ownderId);

    List<ItemDto> searchQueryItem(String text);


}
