package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.repository.ShortRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest toShortRequestDto(ShortRequestDto itemRequestDto, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setRequester(requester);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static RequestDto toRequestDto(ItemRequest itemRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        return RequestDto.of(itemRequest.getId(),
                itemRequest.getDescription(),
                formatter.format(itemRequest.getCreated()));
    }

    public static RequestList toRequestList(ItemRequest itemRequest,
                                            List<Item> items) {
        return new RequestList(ItemRequestMapper.toRequestDto(itemRequest),
                items.stream().map(ItemMapper::toItemDto).collect(Collectors.toUnmodifiableList()));
    }
}
