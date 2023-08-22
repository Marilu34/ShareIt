package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.repository.ShortBookingDto;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemDto {

    private long id;


    private String name;

    private String description;


    private Boolean available;

    private ShortBookingDto lastBooking;

    private ShortBookingDto nextBooking;

    private List<CommentDto> comments;

    private long requestId;
}