package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ShortBookingDto {
    private long id;
    private long bookerId;
    private LocalDateTime rentStartTime;
    private LocalDateTime rentEndTime;
}