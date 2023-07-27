package ru.practicum.shareit.item;

import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {

    ItemDto create(long userId, ItemDto item) throws Exception;

    ItemDto update(long userId, long itemId, ItemDto item);

    ItemWithBookingDto find(long userId, long id);

    List<ItemWithBookingDto> findAll(long userId);

    List<ItemDto> search(String text);
}
