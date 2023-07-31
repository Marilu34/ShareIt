package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnknownStateException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestBody BookingCreationDto bookingCreationDto,
                             @RequestHeader("X-Sharer-User-Id") long bookerId) {
        bookingCreationDto.setBookerId(bookerId);
        BookingDto bookingDto = bookingService.create(bookingCreationDto);
        log.info("Created new booking {}", bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto ownerAcceptation(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                       @PathVariable long bookingId,
                                       @RequestParam boolean approved) {
        BookingDto bookingDto = bookingService.ownerAcceptation(bookingId, ownerId, approved);
        log.info("User {} booking {}", approved ? "approved" : "rejected", bookingId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingByOwnerOrBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long bookingId) {
        BookingDto bookingDto = bookingService.findBookingByOwnerOrBooker(bookingId, userId);
        log.info("Was given booking {} for user {}", bookingId, userId);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> findBookingsOfBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        List<BookingDto> bookings = bookingService.findAllBookingsOfBooker(bookerId, getBookingSearchState(state));
        log.info("For booker {} was found {} bookings with state {}", bookerId, bookings.size(), state);
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingDto> findBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        List<BookingDto> bookings = bookingService.findAllBookingsOfOwner(ownerId, getBookingSearchState(state));
        log.info("For owner {} was found {} bookings with state {}", ownerId, bookings.size(), state);
        return bookings;
    }

    private State getBookingSearchState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException(state);
        }
    }
}
