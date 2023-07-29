package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.NotFoundException;

import javax.validation.Valid;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {

    @Autowired
    private final BookingService service;

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody BookingDto bookingDto) throws Exception {
        log.info("Получен запрос на бронирование");
        return service.createBooking(userId, bookingDto);
    }


    @PatchMapping("/{bookingId}")
    public Booking confirmationOrRejection(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long bookingId,
                                           @RequestParam(name = "approved", required = true) Boolean approved) {
        log.info("Подтверждение или отклонение запроса на бронирование");
        return service.confirmationOrRejectionBooking(userId, bookingId, approved);
    }


    @GetMapping("/{bookingId}")
    public Booking find(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        log.info("Получение данных о конкретном бронировании");
        return service.getBooking(userId, bookingId);
    }


    @GetMapping
    public List<Booking> findAll(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestParam(name = "state", defaultValue = "ALL") State state)
            throws NotFoundException {
        log.info("Получение данных о всех бронированиях");
        return service.getAllBooking(userId, state);
    }


    @GetMapping("/owner")
    public List<Booking> allUserItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestParam(name = "state", defaultValue = "ALL") State state)
            throws NotFoundException {
        log.info("Получение списка бронирований для всех вещей текущего пользователя.");
        return service.getAllUsersItems(userId, state);
    }


    @ExceptionHandler({ConversionFailedException.class})
    public ResponseEntity<ErrorResponse> handleException(ConversionFailedException exc) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(
                new ErrorResponse("Unknown state: UNSUPPORTED_STATUS"), status
        );
    }
}
