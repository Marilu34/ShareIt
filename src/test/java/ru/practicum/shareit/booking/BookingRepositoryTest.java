package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = "db.name=testBookingRepository")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private LocalDateTime now = LocalDateTime.now();

    private Booking booking;
    private Booking booking1;


    @Test
    void testGetBookingsById() {
        long bookingId = booking1.getId();
        long bookerId = booking1.getBooker().getId();
        long ownerId = booking1.getItem().getOwner().getId();
        assertTrue(bookingRepository.findBookingByOwnerOrBooker(bookingId, bookerId).isPresent());
        assertEquals(bookingRepository.findBookingByOwnerOrBooker(bookingId, bookerId).get(),
                bookingRepository.findBookingByOwnerOrBooker(bookingId, ownerId).get());
    }

    @Test
    void testGetAll() {
        long bookerId = booking.getBooker().getId();
        Stream<Booking> actual = bookingRepository.findAllCurrentBookerBookings(bookerId, now, PageRequest.of(0, 1000));
        assertEquals(1, actual.count());
        Stream<Booking> allBookerBookings = bookingRepository.findAllByBookerId(bookerId, PageRequest.of(0, 1000));
        assertTrue(allBookerBookings.count() > 1);
    }

    @Test
    void testGetEmptyList() {
        long ownerId = booking1.getItem().getOwner().getId();
        assertEquals(0, bookingRepository.findAllCurrentOwnerBookings(ownerId, now, PageRequest.ofSize(100))
                .count());
    }

    @Test
    void testShouldReturnBookingByOwnerOrBooker() {
        long id = booking1.getId();
        long bookerId = booking1.getBooker().getId();
        long ownerId = booking1.getItem().getOwner().getId();

        assertTrue(bookingRepository.findBookingByOwnerOrBooker(id, bookerId).isPresent());
        assertEquals(bookingRepository.findBookingByOwnerOrBooker(id, bookerId).get(),
                bookingRepository.findBookingByOwnerOrBooker(id, ownerId).get());
    }

    @BeforeAll
    private void beforeAll() {
        for (int i = 1; i <= 5; i++) {
            userRepository.save(User.builder()
                    .name("User" + i)
                    .email("user" + i + "@email.ru")
                    .build());
        }

        for (long i = 1; i <= 4; i++) {
            itemRepository.save(Item.builder()
                    .name("item" + i)
                    .description("Description of item" + i)
                    .available(true)
                    .owner(userRepository.findById(i).get())
                    .request(null)
                    .build());
        }

        booking = new Booking();
        booking.setItem(itemRepository.findById(1L).get());
        booking.setBooker(userRepository.findById(5L).get());
        booking.setStatus(Status.APPROVED);
        booking.setStart(now.minusSeconds(1));
        booking.setEnd(now.plusSeconds(2));
        booking = bookingRepository.save(booking);

        booking1 = new Booking();
        booking1.setItem(itemRepository.findById(2L).get());
        booking1.setBooker(userRepository.findById(5L).get());
        booking1.setStatus(Status.WAITING);
        booking1.setStart(now.plusSeconds(1));
        booking1.setEnd(now.plusSeconds(2));
        booking1 = bookingRepository.save(booking1);

    }

}