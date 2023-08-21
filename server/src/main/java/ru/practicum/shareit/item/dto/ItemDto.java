package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.repository.BookingShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemDto {

    private long id;


    private String name;

    private String description;


    private Boolean available;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private List<CommentDto> comments;

    private long requestId;
}