package ru.practicum.shareit.item;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDbStorage;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemService {


    private final ItemDbStorage items;
    private final UserDbStorage users;

    private final ItemMapper mapper;


    public ItemDto createItem(ItemDto item, Long userId) throws ResponseStatusException {
        User user = users.getUserById(userId);
        return mapper.mapToItemDto(items.createItem(mapper.mapToItem(item), user));
    }


    public ItemDto updateItem(Long itemId, Long userId, ItemDto item) {
        User user = users.getUserById(userId);
        return mapper.mapToItemDto(items.updateItem(itemId, user, mapper.mapToItem(item)));
    }


    public ItemDto getItemByItemId(Long itemId) {
        return mapper.mapToItemDto(items.getItemByItemId(itemId));
    }


    public ItemDto getPersonalItems(Long userId) {
        return ItemDto.builder()
                .items(items.getPersonalItems(userId).stream().map(mapper::mapToItemDto).collect(Collectors.toList()))
                .build();
    }


    public ItemDto getFoundItems(String text) {
        if (text.isBlank()) {
            return ItemDto.builder().items(new ArrayList<>()).build();
        }
        return ItemDto.builder()
                .items(items.getFoundItems(text).stream().map(mapper::mapToItemDto).collect(Collectors.toList()))
                .build();
    }
}