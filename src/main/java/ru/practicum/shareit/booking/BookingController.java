package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.UnknownStateException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody CreationBooking creationBooking,
                                    @RequestHeader("X-Sharer-User-Id") long bookerId) {
        creationBooking.setBookerId(bookerId);
        BookingDto bookingDto = bookingService.createBooking(creationBooking);
        log.info("Создано новое бронирование {}", bookingDto);
        return bookingDto;
    }
    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long bookingId) {
        BookingDto bookingDto = bookingService.getBookingById(bookingId, userId);
        log.info("Было предоставлено бронирование {} для пользователя {}", bookingId, userId);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "from должен быть больше нуля") int from,
            @RequestParam(defaultValue = "10") @Positive(message = "size должен быть больше нуля") int size
    ) {
        List<BookingDto> bookings = bookingService
                .getAllBookingsByBooker(bookerId, checkState(state), from, size);
        log.info("Для букера {} было найдено {} бронирование с состоянием {}", bookerId, bookings.size(), state);
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "from должен быть больше нуля") int from,
            @RequestParam(defaultValue = "10") @Positive(message = "size должен быть больше нуля") int size
    ) {
        List<BookingDto> bookings = bookingService
                .getAllBookingsByOwner(ownerId, checkState(state), from, size);
        log.info("Для Владельца {} было найдено {} бронирование с состоянием {}", ownerId, bookings.size(), state);
        return bookings;
    }


    @PatchMapping("/{bookingId}")
    public BookingDto confirmationBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                          @PathVariable long bookingId,
                                          @RequestParam boolean approved) {
        BookingDto bookingDto = bookingService.confirmationBooking(bookingId, ownerId, approved);
        String action = approved ? "подтвердил" : "отменил";
        log.info("Пользователь {} {} бронирование {}", ownerId, action, bookingId);
        return bookingDto;
    }

    private State checkState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnknownStateException(state);
        }
    }
}
