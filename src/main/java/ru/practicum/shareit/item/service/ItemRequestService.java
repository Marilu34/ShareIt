package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ShortRequestDto;
import ru.practicum.shareit.item.dto.RequestDto;
import ru.practicum.shareit.item.itemBooking.dto.RequestList;

import java.util.List;

public interface ItemRequestService {
    RequestDto createRequests(ShortRequestDto itemRequestDto);

    List<RequestList> getAllRequestsBySearcher(long requesterId);

    List<RequestList> getAllRequests(long userId, int from, int size);

    RequestList getRequestById(long userId, long requestId);
}
