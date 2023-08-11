package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(CreationBooking creationBooking);

    BookingDto getBookingById(long bookingId, long userId);

    List<BookingDto> getAllBookingsByBooker(long bookerId, State state);

    List<BookingDto> getAllBookingsByOwner(long ownerId, State state);

    BookingDto confirmationBooking(long bookingId, long ownerId, boolean approved);
}

