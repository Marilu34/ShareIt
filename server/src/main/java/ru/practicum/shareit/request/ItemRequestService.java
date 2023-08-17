package ru.practicum.shareit.request;
import ru.practicum.shareit.request.repository.ShortRequestDto;

import java.util.List;

public interface ItemRequestService {
    RequestDto createRequests(ShortRequestDto itemRequestDto);

    List<RequestList> getAllRequestsBySearcher(long requesterId);

    List<RequestList> getAllRequests(long userId, int from, int size);

    RequestList getRequestById(long userId, long requestId);
}
