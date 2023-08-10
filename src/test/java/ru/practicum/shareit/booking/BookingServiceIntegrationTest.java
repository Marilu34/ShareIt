package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolationException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {"db.name=testBooking"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingServiceIntegrationTest {  //legacy test, before theme about Mockito and integrated tests
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    private static CreationBooking creationDto1;
    private static CreationBooking creationDto2;

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

        creationDto1 = new CreationBooking();
        creationDto1.setBookerId(5L);
        creationDto1.setItemId(1L);
        creationDto1.setStart(LocalDateTime.now().plusMinutes(20));
        creationDto1.setEnd(creationDto1.getStart().plusMinutes(40));

        creationDto2 = new CreationBooking();
        creationDto2.setBookerId(6L);
        creationDto2.setItemId(2L);
        creationDto2.setStart(LocalDateTime.now().plusSeconds(1));
        creationDto2.setEnd(creationDto2.getStart().plusSeconds(2));

    }

    @Test
    void create() {
        BookingDto actual = bookingService.createBooking(creationDto1);
        assertEquals(1L, actual.getId());
        assertEquals(5L, actual.getBooker().getId());
        assertEquals(1L, actual.getItem().getId());
        assertEquals(Status.WAITING.name(), actual.getStatus());

        //user does not exists
        creationDto2.setBookerId(100L);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(creationDto2));
        creationDto2.setBookerId(6L);

        //item does not exists
        creationDto2.setItemId(100L);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(creationDto2));
        creationDto2.setItemId(2L);

        //start in past
        creationDto2.setStart(LocalDateTime.now().minusNanos(1));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(creationDto2));

        //end before start
        creationDto2.setStart(creationDto2.getEnd().plusSeconds(30));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(creationDto2));

        //start is null
        creationDto2.setStart(null);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(creationDto2));

        //item is not available
        creationDto2.setStart(creationDto2.getEnd().minusSeconds(1));
        ItemDto item = itemService.getByItemId(creationDto2.getItemId());
        item.setAvailable(false);
        itemService.updateItem(2L, item);
        assertFalse(itemService.getByItemId(item.getId()).getAvailable());
        assertThrows(ValidationException.class, () -> bookingService.createBooking(creationDto2));

    }

    @Test
    void ownerAcceptation() {
        BookingDto actual = bookingService.createBooking(creationDto1);
        long bookingId = actual.getId();
        long ownerId = 1;

        //booking not exists
        assertThrows(NotFoundException.class,
                () -> bookingService.confirmationBooking(100, ownerId, true));

        //booking of another owner
        assertThrows(Exception.class, () -> bookingService.confirmationBooking(bookingId, ownerId + 1, true));

        //good acceptance
        BookingDto actualAccepted = bookingService.confirmationBooking(bookingId, ownerId, true);
        assertEquals(Status.APPROVED.name(), actualAccepted.getStatus());

        //wrong try to change the owner decision
        assertThrows(Exception.class, () -> bookingService.confirmationBooking(bookingId, ownerId, false));
    }

    @Test
    void findBookingByOwnerOrBooker() {

            BookingDto actual = bookingService.createBooking(creationDto1);
            long bookingId = actual.getId();
            long ownerId = 1;
            long bookerId = actual.getBooker().getId();

            // by owner
            BookingDto byOwner = bookingService.getBookingById(bookingId, ownerId);
            assertBookingEquals(actual, byOwner);

            // by booker
            BookingDto byBooker = bookingService.getBookingById(bookingId, bookerId);
            assertBookingEquals(actual, byBooker);

            // booking not exists
            assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId + 10, ownerId));

            // not by owner or booker
            assertThrows(NotFoundException.class, () -> bookingService.getBookingById(bookingId, 9));
        }

    private void assertBookingEquals(BookingDto expected, BookingDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBooker().getId(), actual.getBooker().getId());
        assertEquals(expected.getItem(), actual.getItem());

        // Compare start and end times without considering seconds
        LocalDateTime expectedStartTime = LocalDateTime.parse(expected.getStart(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime expectedEndTime = LocalDateTime.parse(expected.getEnd(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime actualStartTime = LocalDateTime.parse(actual.getStart(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime actualEndTime = LocalDateTime.parse(actual.getEnd(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Duration expectedDuration = Duration.between(expectedStartTime, expectedEndTime);
        Duration actualDuration = Duration.between(actualStartTime, actualEndTime);

        assertTrue(expectedDuration.compareTo(actualDuration) == 0);
    }





    @Test
    void findAllBookingsOfBooker() throws InterruptedException {
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


        //get ALL(2)
        assertEquals(2, bookingService.getAllBookingsByBooker(bookerId, State.ALL, from, size).size());

        //get WAITING(1)
        assertEquals(1, bookingService.getAllBookingsByBooker(bookerId,State.WAITING, from, size).size());

        //get REJECTED(0)
        assertEquals(0, bookingService.getAllBookingsByBooker(bookerId, State.REJECTED, from, size).size());

        // get FUTURE(2)
        List<BookingDto> allBookingsOfBooker = bookingService.getAllBookingsByBooker(bookerId, State.FUTURE, from, size);
        assertEquals(2, allBookingsOfBooker.size());
        assertEquals(actual2.getId(), allBookingsOfBooker.get(0).getId()); //check sorting

        //get CURRENT
        assertTrue(bookingService.getAllBookingsByBooker(bookerId, State.CURRENT, from, size).isEmpty());
        Thread.sleep(2000);
        assertFalse(bookingService.getAllBookingsByBooker(bookerId, State.CURRENT, from, size).isEmpty());
    }

    @Test
    void findAllBookingsOfOwner() throws InterruptedException {
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

        //get ALL(1)
        assertEquals(1, bookingService.getAllBookingsByOwner(ownerId, State.ALL, from, size).size());

        //get WAITING(1)
        assertEquals(1, bookingService.getAllBookingsByOwner(ownerId, State.WAITING, from, size).size());
        bookingService.confirmationBooking(actual.getId(), ownerId, false);
        assertTrue(bookingService.getAllBookingsByOwner(ownerId, State.WAITING, from, size).isEmpty());
        assertEquals(1, bookingService.getAllBookingsByOwner(ownerId, State.ALL, from, size).size());


        //get REJECTED(1)
        assertEquals(1, bookingService.getAllBookingsByOwner(ownerId, State.REJECTED, from, size).size());

        //get CURRENT
        assertTrue(bookingService.getAllBookingsByOwner(ownerId, State.CURRENT, from, size).isEmpty());
        Thread.sleep(2000);
        assertFalse(bookingService.getAllBookingsByOwner(ownerId, State.CURRENT, from, size).isEmpty());
    }
}
