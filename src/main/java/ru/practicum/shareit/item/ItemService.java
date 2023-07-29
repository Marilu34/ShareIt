package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item createItem(Long userId, ItemDto item) throws ValidationException, NotFoundException;

    ItemDto updateItem(Long userId, Long itemId, ItemDto item) throws NotFoundException;

    ItemWithBookingDto getItem(Long userId, Long id) throws NotFoundException;

    List<ItemWithBookingDto> getAllItems(Long userId);

    List<ItemDto> searchItem(String text);
}
