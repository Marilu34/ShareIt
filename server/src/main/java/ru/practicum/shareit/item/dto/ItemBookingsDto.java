package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.repository.ShortBookingDto;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemBookingsDto extends ItemDto {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    ShortBookingDto lastBooking;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    ShortBookingDto nextBooking;
}
