package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CreationBooking {
    long itemId;

    @NotNull
    @FutureOrPresent
    LocalDateTime start;

    @NotNull
    LocalDateTime end;

    long bookerId;

    @AssertTrue(message = "Начало должно быть раньше окончания")
    private boolean isStartBeforeEnd() {  //Без этого метода валятся тесты
        return Objects.nonNull(start) && Objects.nonNull(end) && start.isBefore(end);
    }
}
