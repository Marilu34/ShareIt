package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

public interface BookingService {


    Booking create(long userId, BookingDto bookingDto) throws ValidationException;


    Booking confirmationOrRejection(long userId, long bookingId, Boolean approved) throws ValidationException;


    Booking find(long userId, long bookingId);


    List<Booking> findAll(long userId, State state);


    List<Booking> allUserItems(long userId, State state);

}
