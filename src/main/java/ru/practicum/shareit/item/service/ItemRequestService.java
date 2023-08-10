package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.AddItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(AddItemRequestDto itemRequestDto);

    List<ItemRequestWithItemsDto> findAllRequesterRequests(long requesterId);

    List<ItemRequestWithItemsDto> findAllPageable(long userId, int from, int size);

    ItemRequestWithItemsDto getRequestById(long userId, long requestId);
}
