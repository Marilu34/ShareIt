package booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingServiceTest {
    private ItemRepository mockItemRepository;
    private BookingRepository mockBookingRepository;
    private CommentRepository mockCommentRepository;
    private ItemRequestRepository mockItemRequestRepository;
    private UserRepository mockUserRepository;
    private UserService userService;
    private ItemService itemService;
    private BookingService bookingService;

    @BeforeEach
    void beforeEach() {
        mockItemRepository = Mockito.mock(ItemRepository.class);
        mockBookingRepository = Mockito.mock(BookingRepository.class);
        mockCommentRepository = Mockito.mock(CommentRepository.class);
        mockItemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        mockUserRepository = Mockito.mock(UserRepository.class);

        userService = new UserService(mockUserRepository);
        itemService = new ItemService(mockItemRepository, mockBookingRepository, mockCommentRepository, mockItemRequestRepository, userService);
        bookingService = new BookingService(mockBookingRepository, userService, itemService);
    }

    private Booking makeTestBooking(Status status) {
        User booker = new User(1, "name1", "e@mail1.ru");
        Item item = Item.builder()
                .id(1)
                .ownerId(2)
                .name("item name")
                .description("item description")
                .available(true)
                .build();

        return Booking.builder()
                .id(1)
                .booker(booker)
                .item(item)
                .status(status)
                .rentStartDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .rentEndDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(2))
                .build();
    }

    @Test
    void getByIdNormalWay() {
        Booking testBooking = makeTestBooking(Status.APPROVED);

        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testBooking));

        Booking bookingOfBooker = bookingService.getById(1, 1);
        assertEquals(testBooking, bookingOfBooker);

        Booking bookingOfItemOwner = bookingService.getById(1, 2);
        assertEquals(testBooking, bookingOfItemOwner);
    }

    @Test
    void getByIdBookingNotFound() {
        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(1, 1));

        Assertions.assertEquals("Бронирование 1 не найдено", exception.getMessage());
    }

    @Test
    void getByIdBookingOfAnotherUser() {
        Booking testBooking = makeTestBooking(Status.APPROVED);

        Mockito
                .when(mockBookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testBooking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(1, 3));

        Assertions.assertEquals("Бронирование 1 не найдено", exception.getMessage());
    }

    @Test
    void createBookingNormalWay() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        Mockito
                .when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getItem()));

        Mockito
                .when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(testBooking);

        CreateBooking createBooking = CreateBooking.builder().itemId(1).build();

        Booking booking = bookingService.create(createBooking, 1);

        assertEquals(testBooking, booking);
    }

    @Test
    void createBookingItemUnavailable() {
        Booking testBooking = makeTestBooking(Status.WAITING);
        testBooking.getItem().setAvailable(false);

        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        Mockito
                .when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getItem()));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(CreateBooking.builder().itemId(1).build(), 1));

        Assertions.assertEquals("Вещь с id = 1 недоступна для бронирования", exception.getMessage());
    }

    @Test
    void createBookingBookerIsOwner() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(testBooking.getBooker()));

        Mockito
                .when(mockItemRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getItem()));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(CreateBooking.builder().itemId(1).build(), 2));

        Assertions.assertEquals("Вещь с id = 1 не найдена", exception.getMessage());
    }

    @Test
    void approveBookingNormalWayApprove() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockBookingRepository.findById(1L))
                .thenReturn(Optional.of(testBooking));

        Mockito
                .when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));

        Booking booking = bookingService.approve(1, true, 2);

        testBooking.setStatus(Status.APPROVED);
        assertEquals(testBooking, booking);
    }

    @Test
    void approveBookingNormalWayReject() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockBookingRepository.findById(1L))
                .thenReturn(Optional.of(testBooking));

        Mockito
                .when(mockBookingRepository.save(Mockito.any(Booking.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Booking.class));

        Booking booking = bookingService.approve(1, false, 2);

        testBooking.setStatus(Status.REJECTED);
        assertEquals(testBooking, booking);
    }

    @Test
    void approveBookingWhenIncorrectStatus() {
        Booking testBooking = makeTestBooking(Status.APPROVED);

        Mockito
                .when(mockBookingRepository.findById(1L))
                .thenReturn(Optional.of(testBooking));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.approve(1, true, 2));

        Assertions.assertEquals("Бронирование 1 имеет неверный статус для подтверждения", exception.getMessage());
    }

    @Test
    void approveBookingIncorrectOwner() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockBookingRepository.findById(1L))
                .thenReturn(Optional.of(testBooking));

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approve(1, true, 1));

        Assertions.assertEquals("Бронирование 1 не найдено", exception.getMessage());
    }

    @Test
    void getBookingsByBookerIdUserNotFound() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingsByBookerId(1, "ALL", 0, 20));

        Assertions.assertEquals("Пользователь 1 не найден", exception.getMessage());
    }

    @Test
    void getBookingsByBookerIdUnsupportedStatus() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        final UnknownStateException exception = Assertions.assertThrows(
                UnknownStateException.class,
                () -> bookingService.getBookingsByBookerId(1, "UNSUPPORTED", 0, 20));

        Assertions.assertEquals("Unknown state: UNSUPPORTED", exception.getMessage());
    }

    @Test
    void getBookingsByOwnerIdUserNotFound() {
        Mockito
                .when(mockUserRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingsByOwnerId(1, "ALL", 0, 20));

        Assertions.assertEquals("Пользователь 1 не найден", exception.getMessage());
    }

    @Test
    void getBookingsByOwnerIdUnsupportedStatus() {
        Booking testBooking = makeTestBooking(Status.WAITING);

        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.of(testBooking.getBooker()));

        final UnknownStateException exception = Assertions.assertThrows(
                UnknownStateException.class,
                () -> bookingService.getBookingsByOwnerId(1, "UNSUPPORTED", 0, 20));

        Assertions.assertEquals("Unknown state: UNSUPPORTED", exception.getMessage());
    }
}