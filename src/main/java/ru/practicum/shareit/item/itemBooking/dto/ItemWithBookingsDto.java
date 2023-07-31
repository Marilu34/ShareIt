package ru.practicum.shareit.item.itemBooking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.repository.BookingIdAndBookerIdOnly;
import ru.practicum.shareit.item.dto.ItemDto;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithBookingsDto extends ItemDto {
    BookingIdAndBookerIdOnly lastBooking;
    BookingIdAndBookerIdOnly nextBooking;


    public ItemWithBookingsDto(ItemDto itemDto, BookingIdAndBookerIdOnly lastBooking, BookingIdAndBookerIdOnly nextBooking) {
        super(itemDto.getId(), itemDto.getAvailable(), itemDto.getName(), itemDto.getDescription());
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
