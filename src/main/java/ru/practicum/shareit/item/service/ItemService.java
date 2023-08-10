package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemBooking.ItemCommentsDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemBookingsDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto getByItemId(Long itemId);

    Collection<ItemBookingsDto> getItemsByUserId(Long userId, int from, int size);

    Collection<ItemDto> getItemByComment(String text);
    ItemCommentsDto getByItemId(Long itemId, Long requestFromUserId);

    Collection<ItemDto> getItemByText(String text, int from, int size);

    ItemDto updateItem(Long userId, ItemDto itemDto);

    CommentDto postComment(String comment, Long itemId, Long authorId);
}
