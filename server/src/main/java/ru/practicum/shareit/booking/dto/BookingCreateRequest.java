package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Класс для данных для создания бронирования
 */
@Getter
@Builder
public class BookingCreateRequest {
    private long itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}