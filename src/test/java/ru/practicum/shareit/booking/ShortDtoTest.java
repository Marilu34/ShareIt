package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import static org.junit.jupiter.api.Assertions.*;

public class ShortDtoTest {
    @Test
    void testGetId() {
        // Arrange
        long expectedId = 10;
        ShortBookingDto shortBooking = new ShortBookingDto(1L,expectedId);

        // Act
        shortBooking.setId(expectedId);
        long actualId = shortBooking.getId();

        // Assert
        assertEquals(expectedId, actualId);
    }

    @Test
    void testGetBookerId() {
        // Arrange
        long expectedBookerId = 5;
        ShortBookingDto shortBooking = new ShortBookingDto(1L,expectedBookerId);

        // Act
        shortBooking.setBookerId(expectedBookerId);
        long actualBookerId = shortBooking.getBookerId();

        // Assert
        assertEquals(expectedBookerId, actualBookerId);
    }

    @Test
    void testEqualssameObjectreturnTrue() {

        // Arrange
        ShortBookingDto shortBooking = new ShortBookingDto(11L,12L);

        // Act & Assert
        assertTrue(shortBooking.equals(shortBooking));
    }

    @Test
    void testEqualsdifferentClassreturnFalse() {
        // Arrange
        ShortBookingDto shortBooking = new ShortBookingDto(11L,12L);
        String otherObject = "some string";

        // Act & Assert
        assertFalse(shortBooking.equals(otherObject));
    }

    @Test
    void testEqualsequalObjectsreturnTrue() {
        // Arrange
        Long id = 10L;
        Long bookerId = 5L;
        ShortBookingDto shortBooking1 = new ShortBookingDto(11L,12L);
        shortBooking1.setId(id);
        shortBooking1.setBookerId(bookerId);

        ShortBookingDto shortBooking2 = new ShortBookingDto(13L,14L);
        shortBooking2.setId(id);
        shortBooking2.setBookerId(bookerId);

        // Act & Assert
        assertTrue(shortBooking1.equals(shortBooking2));
    }

    @Test
    void testEqualsdifferentIdsreturnFalse() {
        // Arrange
        Long id1 = 10L;
        Long id2 = 20L;
        Long bookerId = 5L;
        ShortBookingDto shortBooking1 = new ShortBookingDto(id1,bookerId);
        shortBooking1.setId(id1);
        shortBooking1.setBookerId(bookerId);

        ShortBookingDto shortBooking2 = new ShortBookingDto(id2, bookerId);
        shortBooking2.setId(id2);
        shortBooking2.setBookerId(bookerId);

        // Act & Assert
        assertFalse(shortBooking1.equals(shortBooking2));
    }

    @Test
    void testEqualsdifferentBookerIdsreturnFalse() {
        // Arrange
        Long id = 10L;
        Long bookerId1 = 5L;
        Long bookerId2 = 8L;
        ShortBookingDto shortBooking1 = new ShortBookingDto(id,bookerId1);
        shortBooking1.setId(id);
        shortBooking1.setBookerId(bookerId1);

        ShortBookingDto shortBooking2 = new ShortBookingDto(id,bookerId2);
        shortBooking2.setId(id);
        shortBooking2.setBookerId(bookerId2);

        // Act & Assert
        assertFalse(shortBooking1.equals(shortBooking2));
    }
}
