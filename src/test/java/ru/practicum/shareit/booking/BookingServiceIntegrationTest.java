package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"db.name=testBooking"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingServiceIntegrationTest {
    private static CreationBooking creationBooking;
    private static CreationBooking creationBooking1;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;

    private void assertBookingEquals(BookingDto expected, BookingDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem(), actual.getItem());

        LocalDateTime expectedStartTime = LocalDateTime.parse(expected.getStart(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime expectedEndTime = LocalDateTime.parse(expected.getEnd(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime actualStartTime = LocalDateTime.parse(actual.getStart(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime actualEndTime = LocalDateTime.parse(actual.getEnd(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Duration expectedDuration = Duration.between(expectedStartTime, expectedEndTime);
        Duration actualDuration = Duration.between(actualStartTime, actualEndTime);

        assertTrue(expectedDuration.compareTo(actualDuration) == 0);
    }

    @Test
    void testCreateBooking() {
        // Создаем бронирование
        BookingDto actual = bookingService.createBooking(creationBooking);


        // Проверяем, что бронирование с неверным идентификатором бронирующего вызывает NotFoundException
        creationBooking.setBookerId(100L);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(creationBooking));

        // Проверяем, что бронирование с некорректным временем начала вызывает ValidationException
        creationBooking.setBookerId(5L);
        creationBooking.setStart(LocalDateTime.now().minusNanos(1));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(creationBooking));

        // Проверяем, что бронирование с некорректным временем в конце вызывает ValidationException
        creationBooking.setStart(creationBooking.getEnd().plusSeconds(30));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(creationBooking));

        // Проверяем, что бронирование с отсутствующим временем начала вызывает ValidationException
        creationBooking.setStart(null);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(creationBooking));

        // Проверяем, что бронирование с временем начала раньше времени окончания вызывает ValidationException
        creationBooking.setStart(creationBooking.getEnd().minusSeconds(1));

    }


    @Test
    void testConfirmation() {
        BookingDto actual = bookingService.createBooking(creationBooking);
        long bookingId = actual.getId();
        long ownerId = 1;

        assertThrows(NotFoundException.class,
                () -> bookingService.confirmationBooking(100, ownerId, true));

        assertThrows(Exception.class, () -> bookingService.confirmationBooking(bookingId, ownerId + 1, true));

        BookingDto actualAccepted = bookingService.confirmationBooking(bookingId, ownerId, true);
        assertEquals(Status.APPROVED.name(), actualAccepted.getStatus());

        assertThrows(Exception.class, () -> bookingService.confirmationBooking(bookingId, ownerId, false));
    }

    @Test
    void testGetById() {

        BookingDto actual = bookingService.createBooking(creationBooking);
        long bookingId = actual.getId();
        long ownerId = 1;
        long bookerId = actual.getBooker().getId();

        BookingDto byOwner = bookingService.getBookingById(bookingId, ownerId);
        assertBookingEquals(actual, byOwner);

        BookingDto byBooker = bookingService.getBookingById(bookingId, bookerId);
        assertBookingEquals(actual, byBooker);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId + 10, ownerId));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, 9));
    }


    @Test
    void testGetAllBookingsByBooker() throws InterruptedException {
        int from = 0;
        int size = 10;

        long bookerId = 3;
        BookingDto actual1 = bookingService.createBooking(
                new CreationBooking(5,
                        LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(5),
                        bookerId)
        );
        BookingDto actual2 = bookingService.createBooking(
                new CreationBooking(6,
                        LocalDateTime.now().plusSeconds(3),
                        LocalDateTime.now().plusSeconds(4),
                        bookerId)
        );

        bookingService.confirmationBooking(actual1.getId(), actual1.getItem().getId(), true);


        assertEquals(2, bookingService.getAllBookingsByBooker(bookerId, State.ALL, from, size).size());

        assertEquals(1, bookingService.getAllBookingsByBooker(bookerId, State.WAITING, from, size).size());

        assertEquals(0, bookingService.getAllBookingsByBooker(bookerId, State.REJECTED, from, size).size());

        List<BookingDto> allBookingsOfBooker = bookingService.getAllBookingsByBooker(bookerId, State.FUTURE, from, size);
        assertEquals(2, allBookingsOfBooker.size());
        assertEquals(actual2.getId(), allBookingsOfBooker.get(0).getId()); //check sorting

        assertTrue(bookingService.getAllBookingsByBooker(bookerId, State.CURRENT, from, size).isEmpty());
        Thread.sleep(2000);
        assertFalse(bookingService.getAllBookingsByBooker(bookerId, State.CURRENT, from, size).isEmpty());
    }

    @Test
    void testGetAllBookingsByOwner() throws InterruptedException {
        int from = 0;
        int size = 10;

        long ownerId = 7;
        long bookerId = 8;
        BookingDto actual = bookingService.createBooking(
                new CreationBooking(7,
                        LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(5),
                        bookerId)
        );

        assertEquals(1, bookingService.getAllBookingsByOwner(ownerId, State.ALL, from, size).size());

        assertEquals(1, bookingService.getAllBookingsByOwner(ownerId, State.WAITING, from, size).size());
        bookingService.confirmationBooking(actual.getId(), ownerId, false);
        assertTrue(bookingService.getAllBookingsByOwner(ownerId, State.WAITING, from, size).isEmpty());
        assertEquals(1, bookingService.getAllBookingsByOwner(ownerId, State.ALL, from, size).size());
        assertEquals(1, bookingService.getAllBookingsByOwner(ownerId, State.REJECTED, from, size).size());
        assertTrue(bookingService.getAllBookingsByOwner(ownerId, State.CURRENT, from, size).isEmpty());
        Thread.sleep(2000);
        assertFalse(bookingService.getAllBookingsByOwner(ownerId, State.CURRENT, from, size).isEmpty());
    }

    @BeforeAll
    void before() {
        LongStream.rangeClosed(1, 10)
                .mapToObj((i) -> UserDto.builder()
                        .name(String.format("User%d", i))
                        .email(String.format("email%d@mail.net", i))
                        .build()).forEach(userService::createUser);

        LongStream.rangeClosed(1, 10)
                .mapToObj((i) -> ItemDto.builder()
                        .id(i)
                        .available(true)
                        .name(String.format("Item%d", i))
                        .description(String.format("Item%d description", i))
                        .build())
                .forEach(itemDto -> itemService.createItem(itemDto.getId(), itemDto));

        creationBooking = new CreationBooking();
        creationBooking.setBookerId(5L);
        creationBooking.setItemId(1L);
        creationBooking.setStart(LocalDateTime.now().plusMinutes(20));
        creationBooking.setEnd(creationBooking.getStart().plusMinutes(40));

        creationBooking1 = new CreationBooking();
        creationBooking1.setBookerId(6L);
        creationBooking1.setItemId(2L);
        creationBooking1.setStart(LocalDateTime.now().plusSeconds(1));
        creationBooking1.setEnd(creationBooking1.getStart().plusSeconds(2));

    }

}
