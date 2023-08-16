package ru.practicum.shareit.item.itemBooking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude
public class ItemBookingsDto extends ItemDto {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    ShortBookingDto lastBooking;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    ShortBookingDto nextBooking;

}
