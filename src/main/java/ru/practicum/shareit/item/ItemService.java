package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(long userId, ItemDto item) throws ValidationException, NotFoundException;

    ItemDto update(long userId, long itemId, ItemDto item) throws NotFoundException;

    ItemWithBookingDto find(long userId, long id) throws NotFoundException;

    List<ItemWithBookingDto> findAll(long userId);

    List<ItemDto> search(String text);
}
