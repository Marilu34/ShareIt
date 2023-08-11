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
import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
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
                .available(true)
                .build();
        long bookerId = 11L;
        creationBooking.setBookerId(bookerId);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(creationBooking));
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


}