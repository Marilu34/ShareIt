package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ItemWithBookingDto {

    long id;  //уникальный идентификатор вещи
    String name; // краткое название
    String description; //развёрнутое описание
    Boolean available; //статус о том, доступна или нет вещь для аренды
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
}
