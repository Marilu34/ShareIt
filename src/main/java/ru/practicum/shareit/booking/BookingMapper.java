package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking mapCreationDtoToBooking(CreationBooking creationBooking, Item item, User booker) {
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(creationBooking.getStart());
        booking.setEnd(creationBooking.getEnd());
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public static BookingDto mapBookingToDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart().toString(),
                booking.getEnd().toString(),
                booking.getStatus().name(),
                UserMapper.mapToUserDto(booking.getBooker()),
                ItemMapper.mapToItemDto(booking.getItem())
        );
    }
}
