package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingCreationDto bookingCreationDto);

    BookingDto ownerAcceptation(long bookingId, long ownerId, boolean approved);

    BookingDto findBookingByOwnerOrBooker(long bookingId, long userId);

    List<BookingDto> findAllBookingsOfBooker(long bookerId, State state);

    List<BookingDto> findAllBookingsOfOwner(long ownerId, State state);
}

