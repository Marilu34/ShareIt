package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ShortRequestDto;

import javax.validation.Validation;
import javax.validation.Validator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShortDtoTest {
    @Autowired
    private JacksonTester<ShortRequestDto> json;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private void validate(ShortRequestDto shortRequestDto) {
        List<String> mistakes = new ArrayList<>();

        validator.validate(shortRequestDto).forEach(mistake -> {
            String message = mistake.getPropertyPath() + ": " + mistake.getMessage();
            mistakes.add(message);
        });

        if (!mistakes.isEmpty()) {
            throw new ValidationException("Ошибки: " + mistakes);
        }
    }

    @Test
    void testGetId() {
        // Arrange
        long expectedId = 10;
        ShortBookingDto shortBooking = new ShortBookingDto(1L, expectedId);

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
        ShortBookingDto shortBooking = new ShortBookingDto(1L, expectedBookerId);

        // Act
        shortBooking.setBookerId(expectedBookerId);
        long actualBookerId = shortBooking.getBookerId();

        // Assert
        assertEquals(expectedBookerId, actualBookerId);
    }

    @Test
    void testRiqghEquals() {

        // Arrange
        ShortBookingDto shortBooking = new ShortBookingDto(11L, 12L);

        // Act & Assert
        assertTrue(shortBooking.equals(shortBooking));
    }

    @Test
    void testBadEquals() {
        // Arrange
        ShortBookingDto shortBooking = new ShortBookingDto(11L, 12L);
        String otherObject = "some string";

        // Act & Assert
        assertFalse(shortBooking.equals(otherObject));
    }

    @Test
    void testEquals() {
        // Arrange
        Long id = 10L;
        Long bookerId = 5L;
        ShortBookingDto shortBooking1 = new ShortBookingDto(11L, 12L);
        shortBooking1.setId(id);
        shortBooking1.setBookerId(bookerId);

        ShortBookingDto shortBooking2 = new ShortBookingDto(13L, 14L);
        shortBooking2.setId(id);
        shortBooking2.setBookerId(bookerId);

        // Act & Assert
        assertTrue(shortBooking1.equals(shortBooking2));
    }

    @Test
    void testWrongIds() {
        // Arrange
        Long id1 = 10L;
        Long id2 = 20L;
        Long bookerId = 5L;
        ShortBookingDto shortBooking1 = new ShortBookingDto(id1, bookerId);
        shortBooking1.setId(id1);
        shortBooking1.setBookerId(bookerId);

        ShortBookingDto shortBooking2 = new ShortBookingDto(id2, bookerId);
        shortBooking2.setId(id2);
        shortBooking2.setBookerId(bookerId);

        // Act & Assert
        assertFalse(shortBooking1.equals(shortBooking2));
    }


    @Test
    void testWrongBookerId() {
        // Arrange
        Long id = 10L;
        Long bookerId1 = 5L;
        Long bookerId2 = 8L;
        ShortBookingDto shortBooking1 = new ShortBookingDto(id, bookerId1);
        shortBooking1.setId(id);
        shortBooking1.setBookerId(bookerId1);

        ShortBookingDto shortBooking2 = new ShortBookingDto(id, bookerId2);
        shortBooking2.setId(id);
        shortBooking2.setBookerId(bookerId2);

        // Act & Assert
        assertFalse(shortBooking1.equals(shortBooking2));

    }


    @Test
    void testEmptyDescription() {
        ShortRequestDto dto = new ShortRequestDto();
        //null case
        assertThrows(ValidationException.class, () -> validate(dto));

        //blank case
        dto.setDescription("  ");
        assertThrows(ValidationException.class, () -> validate(dto));

        // correct case
        dto.setDescription("correct description");

        assertDoesNotThrow(() -> validate(dto));
    }


    @Test
    void testWrongDescription() {
        ShortRequestDto dto = new ShortRequestDto();
        dto.setDescription("a".repeat(2024));
        assertDoesNotThrow(() -> validate(dto));

        dto.setDescription("b".repeat(2048) + " ");
        assertThrows(ValidationException.class, () -> validate(dto));
    }
}
