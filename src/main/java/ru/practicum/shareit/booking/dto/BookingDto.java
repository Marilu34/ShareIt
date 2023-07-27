package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.Status;


import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {

    private Long id;  //уникальный идентификатор бронирования
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime start; //дата и время начала бронирования
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime end; //дата и время конца бронирования
    Long itemId; //вещь, которую пользователь бронирует
    Long bookerId; //пользователь, который осуществляет бронирование
    Status status; //статус бронирования
}
