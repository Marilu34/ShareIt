package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

public interface BookingService {


    Booking createBooking(Long userId, BookingDto bookingDto) throws Exception;


    Booking confirmationOrRejectionBooking(Long userId, Long bookingId, Boolean approved) throws ValidationException, NotFoundException;


    Booking getBooking(Long userId, Long bookingId) throws NotFoundException;


    List<Booking> getAllBooking(Long userId, State state) throws NotFoundException;


    List<Booking> getAllUsersItems(Long userId, State state) throws NotFoundException;

}
