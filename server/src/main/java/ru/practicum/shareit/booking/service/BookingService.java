package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;


    public Booking getById(long bookingId, long userId) throws NotFoundException {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new NotFoundException("Бронирование " + bookingId + " не найдено");
        } else {
            Booking booking = optionalBooking.get();
            if (booking.getBooker().getId() == userId || booking.getItem().getOwnerId() == userId) {
                return booking;
            } else {
                throw new NotFoundException("Бронирование " + bookingId + " не найдено");
            }
        }
    }


    public Booking create(BookingCreateRequest bookingCreateRequest, long bookerId) throws ValidationException, NotFoundException {
        User booker = userService.getById(bookerId);
        Item item = itemService.getById(bookingCreateRequest.getItemId(), 0);
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь с id = " + item.getId() + " недоступна для бронирования");
        }

        if (item.getOwnerId() == bookerId) {
            throw new NotFoundException("Вещь с id = " + item.getId() + " не найдена");
        }

        Booking booking = new Booking(0, item, booker, bookingCreateRequest.getStart(), bookingCreateRequest.getEnd(), WAITING);
        booking = bookingRepository.save(booking);
        return booking;
    }


    public Booking approve(long bookingId, boolean approved, long ownerId) throws ValidationException, NotFoundException {
        Booking booking = getById(bookingId, ownerId);
        if (booking.getItem().getOwnerId() == ownerId) {
            if (booking.getStatus() == WAITING) {
                booking.setStatus(approved ? APPROVED : REJECTED);
                return bookingRepository.save(booking);
            } else {
                throw new ValidationException("Бронирование " + bookingId + " имеет неверный статус для подтверждения");
            }
        } else {
            throw new NotFoundException("Бронирование " + bookingId + " не найдено");
        }
    }

    /**
     * Вернуть список бронирований, созданных пользователем
     *
     * @param bookerId id пользователя
     * @param state    вид фильтрации данных: по умолчанию равен ALL, может принимать значения CURRENT, PAST, FUTURE, WAITING, REJECTED
     * @return список объектов Booking
     * @throws BookingUnsupportedStatusException если указан неверный вид фильтрации
     */
    public List<Booking> getBookingsByBookerId(long bookerId, String state, int from, int size) throws BookingUnsupportedStatusException {
        userService.getById(bookerId);
        Pageable page =  PageRequest.of(from / size, size);

        switch (state) {
            case "CURRENT":
                return bookingRepository.findByBooker_idAndRentStartDateBeforeAndRentEndDateAfterOrderByRentStartDateDesc(bookerId, LocalDateTime.now(), LocalDateTime.now(), page);

            case "PAST":
                return bookingRepository.findByBooker_idAndRentEndDateBeforeOrderByRentStartDateDesc(bookerId, LocalDateTime.now(), page);

            case "FUTURE":
                return bookingRepository.findByBooker_idAndRentStartDateAfterOrderByRentStartDateDesc(bookerId, LocalDateTime.now(), page);

            case "WAITING":
                return bookingRepository.findByBooker_idAndStatusOrderByRentStartDateDesc(bookerId, WAITING, page);

            case "REJECTED":
                return bookingRepository.findByBooker_idAndStatusOrderByRentStartDateDesc(bookerId, REJECTED, page);

            case "ALL":
                return bookingRepository.findByBooker_idOrderByRentStartDateDesc(bookerId, page);

            default:
                throw new BookingUnsupportedStatusException("Unknown state: " + state);
        }
    }

    /**
     * Вернуть список бронирований вещей пользователя
     *
     * @param ownerId id пользователя
     * @param state   вид фильтрации данных: по умолчанию равен ALL, может принимать значения CURRENT, PAST, FUTURE, WAITING, REJECTED
     * @return список объектов Booking
     * @throws BookingUnsupportedStatusException если указан неверный вид фильтрации
     */
    public List<Booking> getBookingsByOwnerId(long ownerId, String state, int from, int size) {
        userService.getById(ownerId);
        Pageable page =  PageRequest.of(from / size, size);

        switch (state) {
            case "CURRENT":
                return bookingRepository.findBookingsOfItemsByOwnerIdInCurrent(ownerId, LocalDateTime.now(), page);

            case "PAST":
                return bookingRepository.findBookingsOfItemsByOwnerIdInPast(ownerId, LocalDateTime.now(), page);

            case "FUTURE":
                return bookingRepository.findBookingsOfItemsByOwnerIdInFuture(ownerId, LocalDateTime.now(), page);

            case "WAITING":
                return bookingRepository.findBookingsOfItemsByOwnerIdAndStatus(ownerId, WAITING, page);

            case "REJECTED":
                return bookingRepository.findBookingsOfItemsByOwnerIdAndStatus(ownerId, REJECTED, page);

            case "ALL":
                return bookingRepository.findBookingsOfItemsByOwnerId(ownerId, page);

            default:
                throw new BookingUnsupportedStatusException("Unknown state: " + state);
        }
    }
}