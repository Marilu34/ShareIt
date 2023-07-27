package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

public interface BookingService {


    Booking create(long userId, BookingDto bookingDto) throws ValidationException, NotFoundException;


    Booking confirmationOrRejection(long userId, long bookingId, Boolean approved) throws ValidationException, NotFoundException;


    Booking find(long userId, long bookingId) throws NotFoundException;


    List<Booking> findAll(long userId, State state) throws NotFoundException;


    List<Booking> allUserItems(long userId, State state) throws NotFoundException;

}
