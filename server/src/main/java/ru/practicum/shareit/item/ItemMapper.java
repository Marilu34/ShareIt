package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.repository.ShortBookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingsDto;
import ru.practicum.shareit.item.dto.ItemCommentsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .available(item.isAvailable())
                .name(item.getName())
                .description(item.getDescription())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item fromItemDto(ItemDto itemDto, User owner, ItemRequest request) {
        return Item.builder()
                .id(Optional.ofNullable(itemDto.getId()).orElse(0L))
                .available(itemDto.getAvailable())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(owner)
                .request(request)
                .build();
    }

    public static ItemBookingsDto toItemBookingsDto(Item item,
                                                    ShortBookingDto lastBooking,
                                                    ShortBookingDto nextBooking) {
        ItemBookingsDto result = new ItemBookingsDto();
        result.setId(item.getId());
        result.setName(item.getName());
        result.setDescription(item.getDescription());
        result.setAvailable(item.isAvailable());
        result.setLastBooking(lastBooking);
        result.setNextBooking(nextBooking);
        result.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return result;
    }


    public static ItemCommentsDto toItemCommentDto(Item item,
                                                   ShortBookingDto lastBooking,
                                                   ShortBookingDto nextBooking,
                                                   List<CommentDto> comments) {
        ItemCommentsDto result = new ItemCommentsDto();
        result.setId(item.getId());
        result.setName(item.getName());
        result.setDescription(item.getDescription());
        result.setAvailable(item.isAvailable());
        result.setLastBooking(lastBooking);
        result.setNextBooking(nextBooking);
        result.setComments(comments);
        result.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return result;
    }
}

