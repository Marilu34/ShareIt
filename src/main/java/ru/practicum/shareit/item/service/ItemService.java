package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemBooking.ItemCommentsDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemBookingsDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemCommentsDto getByItemId(Long itemId, Long requestFromUserId);

    Collection<ItemBookingsDto> getItemsByUserId(Long userId);

    Collection<ItemDto> getItemByComment(String text);

    ItemDto updateItem(Long userId, ItemDto itemDto);

    CommentDto postCommentForItemFromAuthor(String text, Long itemId, Long authorId);
}
