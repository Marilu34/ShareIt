package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingDtoMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemDtoMapper {

    public static Item toItem(ItemDto itemDto) {
        if (itemDto != null) {
            return Item.builder()
                    .id(itemDto.getId())
                    .name(itemDto.getName())
                    .description(itemDto.getDescription())
                    .available(itemDto.getAvailable()).build();
        } else {
            return null;
        }
    }

    public static ItemDto toItemDto(Item item) {
        if (item != null) {
            ItemDto itemDto = ItemDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .lastBooking(BookingDtoMapper.toBookingShortDto(item.getLastBooking()))
                    .nextBooking(BookingDtoMapper.toBookingShortDto(item.getNextBooking()))
                    .comments(CommentDtoMapper.toCommentDtoList(item.getComments())).build();

            if (item.getItemRequest() != null) {
                itemDto.setRequestId(item.getItemRequest().getRequestId());
            }

            return itemDto;
        } else {
            return null;
        }
    }


    public static List<ItemDto> toItemDtoList(List<Item> itemList) {
        List<ItemDto> itemDtoList = new ArrayList<>();

        if (itemList != null) {
            for (Item item : itemList) {
                itemDtoList.add(toItemDto(item));
            }
        }

        return itemDtoList;
    }

    public static Item toItem(ItemCreateRequest itemCreateRequest) {
        if (itemCreateRequest != null) {
            return Item.builder()
                    .name(itemCreateRequest.getName())
                    .description(itemCreateRequest.getDescription())
                    .available(itemCreateRequest.getAvailable()).build();
        } else {
            return null;
        }
    }

    public static ShortItemsDto toItemSmallDto(Item item) {
        if (item != null) {
            ShortItemsDto shortItemsDto = ShortItemsDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable()).build();

            if (item.getItemRequest() != null) {
                shortItemsDto.setRequestId(item.getItemRequest().getRequestId());
            }

            return shortItemsDto;
        } else {
            return null;
        }
    }

    public static List<ShortItemsDto> toItemSmallDtoList(List<Item> itemList) {
        List<ShortItemsDto> shortItemsDtoList = new ArrayList<>();

        if (itemList != null) {
            for (Item item : itemList) {
                shortItemsDtoList.add(toItemSmallDto(item));
            }
        }

        return shortItemsDtoList;
    }
}