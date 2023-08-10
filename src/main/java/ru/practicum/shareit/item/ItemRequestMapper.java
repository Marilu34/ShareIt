package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ShortRequestDto;
import ru.practicum.shareit.item.dto.RequestDto;
import ru.practicum.shareit.item.itemBooking.dto.RequestList;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequest mapToItemRequest(ShortRequestDto itemRequestDto, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(requester);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static RequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return RequestDto.of(itemRequest.getId(),
                itemRequest.getDescription(),
                formatter.format(itemRequest.getCreated()));
    }

    public static RequestList mapToItemRequestWithItemsDto(ItemRequest itemRequest,
                                                           List<Item> items) {
        return new RequestList(ItemRequestMapper.mapToItemRequestDto(itemRequest),
                items.stream().map(ItemMapper::toItemDto).collect(Collectors.toUnmodifiableList()));
    }
}
