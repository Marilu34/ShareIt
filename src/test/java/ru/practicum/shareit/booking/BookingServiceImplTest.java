package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    private CreationBooking creationBooking;
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;


    @Spy
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    void testCreate() {
        long ownerId = 123;
        Item item = Item.builder()
                .id(1)
                .owner(User.builder().id(ownerId).build())
                .available(false) // Set item availability to false
                .build();
        long bookerId = ownerId; // Set bookerId same as ownerId
        creationBooking.setBookerId(bookerId);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(creationBooking));
    }


    @Test
    void testGetAll() {
        when(userRepository.existsById(123L)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByOwner(123L, State.ALL, 0, 1));
        verifyNoInteractions(bookingRepository);
    }


    @BeforeEach
    void before() {
        creationBooking = new CreationBooking();
        creationBooking.setBookerId(1);
        creationBooking.setItemId(1);
        creationBooking.setStart(LocalDateTime.now().plusSeconds(2));
        creationBooking.setEnd(LocalDateTime.now().plusSeconds(3));

        when(userRepository.existsById(anyLong())).thenReturn(true);

    }


    @Test
    void testGetAllBookingsIfUserNotExists() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByOwner(1, State.ALL, 0, 1));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void testGetAllBookings() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingsByOwner(1, State.ALL, 0, 1));
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void testGetAllPastBookings() {
        bookingService.getAllBookingsByOwner(1, State.PAST, 0, 1);

        verify(bookingRepository, atLeastOnce())
                .findAllByItemOwnerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testGetAllAFutureBookings() {
        bookingService.getAllBookingsByOwner(1, State.FUTURE, 0, 1);

        verify(bookingRepository, atLeastOnce())
                .findAllByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void testGetAllApprovedBookings() {
        bookingService.getAllBookingsByOwner(1, State.APPROVED, 0, 1);

        verify(bookingRepository, atLeastOnce())
                .findAllByItemOwnerIdAndStatusIs(anyLong(), any(Status.class), any(Pageable.class));
    }

    @Test
    void testGetAllBookingsByBooker_PastState() {
        long bookerId = 123;
        LocalDateTime currentDateTime = LocalDateTime.now();
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));

        when(userRepository.existsById(bookerId)).thenReturn(true);

        bookingService.getAllBookingsByBooker(bookerId, State.PAST, 0, 10);

        verify(bookingRepository).findAllByBookerIdAndEndIsBefore(bookerId, currentDateTime, page);
    }

    @Test
    void testGetAllBookingsByBooker_ApprovedState() {
        long bookerId = 123;
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));

        when(userRepository.existsById(bookerId)).thenReturn(true);

        bookingService.getAllBookingsByBooker(bookerId, State.APPROVED, 0, 10);

        verify(bookingRepository).findAllByBookerIdAndStatusIs(bookerId, Status.APPROVED, page);
    }


}