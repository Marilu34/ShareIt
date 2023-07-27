package ru.practicum.shareit.item;

import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;

import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }

    public static Item fromItemDto(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;

    }

    public static ItemWithBookingDto toItemWithBookingDTO(Item item) {
        ItemWithBookingDto itemWithBookingDTO = new ItemWithBookingDto();
        itemWithBookingDTO.setId(item.getId());
        itemWithBookingDTO.setName(item.getName());
        itemWithBookingDTO.setDescription(item.getDescription());
        itemWithBookingDTO.setAvailable(item.getAvailable());
        itemWithBookingDTO.setComments(item.getComments().stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return itemWithBookingDTO;
    }

    public static Item fromItemWithBookingDTO(ItemWithBookingDto itemWithBookingDTO) {
        Item item = new Item();
        item.setId(itemWithBookingDTO.getId());
        item.setName(itemWithBookingDTO.getName());
        item.setDescription(itemWithBookingDTO.getDescription());
        item.setAvailable(itemWithBookingDTO.getAvailable());
        return item;

    }
}
