package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

class BookingMapperTest {

    @Test
    public void fromBookingDto_ReturnsCorrectBooking() {
        // Arrange
        CreationBooking creationBooking = new CreationBooking();
        creationBooking.setStart(LocalDateTime.of(2022, 1, 1, 8, 0));
        creationBooking.setEnd(LocalDateTime.of(2022, 1, 1, 10, 0));

        Item item = new Item();
        item.setId(1L);

        User booker = new User();
        booker.setId(2L);

        // Act
        Booking booking = BookingMapper.fromBookingDto(creationBooking, item, booker);

        // Assert
        Assertions.assertEquals(booker, booking.getBooker());
        Assertions.assertEquals(item, booking.getItem());
        Assertions.assertEquals(LocalDateTime.of(2022, 1, 1, 8, 0), booking.getStart());
        Assertions.assertEquals(LocalDateTime.of(2022, 1, 1, 10, 0), booking.getEnd());
        Assertions.assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    public void toBookingDto_ReturnsCorrectBookingDto() {
        // Arrange
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.of(2022, 1, 1, 8, 0));
        booking.setEnd(LocalDateTime.of(2022, 1, 1, 10, 0));
        booking.setStatus(Status.WAITING);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(3L);

        // Act
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        // Assert
        Assertions.assertEquals(booking.getId(), bookingDto.getId());
        Assertions.assertEquals("2022-01-01T08:00", bookingDto.getStart());
        Assertions.assertEquals("2022-01-01T10:00", bookingDto.getEnd());
        Assertions.assertEquals("WAITING", bookingDto.getStatus());
        Assertions.assertEquals(booker.getId(), bookingDto.getBooker().getId());
        Assertions.assertEquals(item.getId(), bookingDto.getItem().getId());
    }
}
