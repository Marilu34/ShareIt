package ru.practicum.shareit.item.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ShortRequestDto;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JsonTest
class ShortRequestDtoTest {

    @Autowired
    private JacksonTester<ShortRequestDto> json;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldNotAcceptBlankDescription() {
        ShortRequestDto dto = new ShortRequestDto();
        //null case
        assertThrows(ConstraintViolationException.class, () -> validate(dto));

        //blank case
        dto.setDescription("  ");
        assertThrows(ConstraintViolationException.class, () -> validate(dto));

        // correct case
        dto.setDescription("correct description");

        assertDoesNotThrow(() -> validate(dto));
    }

    @Test
    void shouldNotAcceptLongerThan2048CharsDescription() {
        ShortRequestDto dto = new ShortRequestDto();
        dto.setDescription("a".repeat(2024));
        assertDoesNotThrow(() -> validate(dto));

        dto.setDescription("b".repeat(2048) + " ");
        assertThrows(ConstraintViolationException.class, () -> validate(dto));
    }

    @SneakyThrows
    @Test
    void shouldDeserializeCorrectly() {
        ShortRequestDto dto = new ShortRequestDto();
        dto.setDescription("description");
        dto.setRequesterId(5L);

        JsonContent<ShortRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(dto.getDescription());
    }

    private void validate(ShortRequestDto o) {
        Set<ConstraintViolation<ShortRequestDto>> violations = validator.validate(o);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}