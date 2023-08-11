package ru.practicum.shareit.item.itemBooking.dto;

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
public class ItemBookingsDto extends ItemDto {
    ShortBookingDto lastBooking;
    ShortBookingDto nextBooking;


    public ItemBookingsDto(ItemDto itemDto, ShortBookingDto lastBooking, ShortBookingDto nextBooking) {
        super(itemDto.getId(), itemDto.getAvailable(), itemDto.getName(), itemDto.getDescription());
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
