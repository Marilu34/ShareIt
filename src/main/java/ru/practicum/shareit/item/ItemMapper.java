package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemBooking.ItemCommentsDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .available(item.isAvailable())
                .name(item.getName())
                .description(item.getDescription())
                .build();
    }

    public static Item fromItemDto(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(Optional.ofNullable(itemDto.getId()).orElse(0L))
                .available(itemDto.getAvailable())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(owner)
                .build();
    }

    public static ItemBookingsDto toItemBookingsDto(Item item,
                                                    ShortBookingDto lastBooking,
                                                    ShortBookingDto nextBooking) {
        return new ItemBookingsDto(toItemDto(item), lastBooking, nextBooking);
    }

    public static ItemCommentsDto toItemCommentDto(Item item,
                                                   ShortBookingDto lastBooking,
                                                   ShortBookingDto nextBooking,
                                                   List<CommentDto> comments) {
        return new ItemCommentsDto(toItemBookingsDto(item, lastBooking, nextBooking), comments);
    }
}

