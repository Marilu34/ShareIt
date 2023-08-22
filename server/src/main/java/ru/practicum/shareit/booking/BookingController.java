package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.Constants;

import javax.validation.ValidationException;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestBody BookingCreateRequest bookingCreateRequest,
                             @RequestHeader(value = Constants.X_HEADER_NAME) long bookerId) {
        log.info("Пользователем {} создано бронирование: " + bookingCreateRequest.toString(), bookerId);
        return BookingDtoMapper.toBookingDto(bookingService.create(bookingCreateRequest, bookerId));
    }


    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approve(@PathVariable long bookingId,
                              @RequestParam Boolean approved,
                              @RequestHeader(value = Constants.X_HEADER_NAME) int ownerId) {
        log.info("Бронирование {} пользователем {} одобрено {}", bookingId, ownerId, approved);
        return BookingDtoMapper.toBookingDto(bookingService.approve(bookingId, approved, ownerId));
    }


    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto get(@PathVariable long bookingId,
                          @RequestHeader(value = Constants.X_HEADER_NAME) int userId) {
        log.info("Получено бронирование {} от Пользователя {}", bookingId, userId);
        return BookingDtoMapper.toBookingDto(bookingService.getById(bookingId, userId));
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingsByBookerId(@RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "20") int size,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader(value = Constants.X_HEADER_NAME) int bookerId) {
        log.info("Получены бронирования от Пользоваеля {}", bookerId);
        return BookingDtoMapper.toBookingDtoList(bookingService.getBookingsByBookerId(bookerId, state, from, size));
    }


    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getBookingsByOwnerId(@RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @RequestHeader(value = Constants.X_HEADER_NAME) int ownerId) {
        log.info("Получать заказы на товар с id владельца {}", ownerId);
        return BookingDtoMapper.toBookingDtoList(bookingService.getBookingsByOwnerId(ownerId, state, from, size));
    }
}