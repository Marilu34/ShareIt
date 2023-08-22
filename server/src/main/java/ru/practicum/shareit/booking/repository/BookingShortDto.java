package ru.practicum.shareit.booking.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@AllArgsConstructor
public class BookingShortDto {
    private long id;
    private long bookerId;
    private LocalDateTime rentStartTime;
    private LocalDateTime rentEndTime;
}