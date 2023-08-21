package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
public class CreationBooking {
    @Positive
    private long itemId;

    @FutureOrPresent
    @NotNull
    private LocalDateTime start;

    @Future
    @NotNull
    private LocalDateTime end;

    @AssertTrue(message = "Начало должно быть раньше окончания")
    private boolean isStartBeforeEnd() {  //Без этого метода валятся тесты
        return Objects.nonNull(start) && Objects.nonNull(end) && start.isBefore(end);
    }

}