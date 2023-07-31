package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemBooking.ItemWithBookingsAndCommentsDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemWithBookingsDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, ItemDto itemDto);


    ItemWithBookingsAndCommentsDto getByItemId(Long itemId, Long requestFromUserId);

    Collection<ItemWithBookingsDto> getByUserId(Long userId);

    Collection<ItemDto> findByText(String text);


    CommentDto postCommentForItemFromAuthor(String text, Long itemId, Long authorId);
}
