package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final Validator validator;

    @Transactional
    @Override
    public BookingDto createBooking(CreationBooking creationBooking) {
        validation(creationBooking);
        Item item = itemRepository.findById(creationBooking.getItemId())
                .orElseThrow(() -> new NotFoundException("объект Item не найден в репозитории"));
        if (!item.isAvailable()) {
            throw new ValidationException("Объект Item не доступен");
        }
        if (item.getOwner().getId() == creationBooking.getBookerId()) {
            throw new NotFoundException("Владелец не может забронировать собственную вещь");
        }
        User booker = userRepository.findById(creationBooking.getBookerId())
                .orElseThrow(() -> new NotFoundException("объект User не найден в репозитории"));
        Booking booking = bookingRepository.save(BookingMapper.fromBookingDto(creationBooking, item, booker));

        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto confirmationBooking(long bookingId, long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("объект Booking не найден в репозитории"));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new NotFoundException("Только владелец может подтвердить бронирование");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ValidationException("Объект Booking имеет отличный статус от WAITING");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository.findBookingByOwnerOrBooker(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("объект Booking не найден в репозитории"));
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsByBooker(long bookerId, State state) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("объект Booker не найден в репозитории");
        }
        Stream<Booking> bookingStream;
        final Sort sort = Sort.sort(Booking.class).by(Booking::getStart).descending();
        switch (state) {
            case ALL:
                bookingStream = bookingRepository.findAllByBookerId(bookerId, sort);
                break;
            case PAST:
                bookingStream = bookingRepository.findAllByBookerIdAndEndIsBefore(bookerId, LocalDateTime.now(), sort);
                break;
            case CURRENT:
                bookingStream = bookingRepository.findAllCurrentBookerBookings(bookerId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookingStream = bookingRepository.findAllByBookerIdAndStartIsAfter(bookerId, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookingStream = bookingRepository.findAllByBookerIdAndStatusIs(bookerId, Status.WAITING, sort);
                break;
            case APPROVED:
                bookingStream = bookingRepository.findAllByBookerIdAndStatusIs(bookerId, Status.APPROVED, sort);
                break;
            case REJECTED:
                bookingStream = bookingRepository.findAllByBookerIdAndStatusIs(bookerId, Status.REJECTED, sort);
                break;
            default:
                throw new NotYetImplementedException();

        }
        return bookingStream.map(BookingMapper::toBookingDto).collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsByOwner(long ownerId, State state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("объект Владелец не найден в репозитории");
        }
        Stream<Booking> bookingStream;
        final Sort sort = Sort.sort(Booking.class).by(Booking::getStart).descending();
        switch (state) {
            case ALL:
                bookingStream = bookingRepository.findAllByItemOwnerId(ownerId, sort);
                break;
            case PAST:
                bookingStream = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(ownerId, LocalDateTime.now(), sort);
                break;
            case CURRENT:
                bookingStream = bookingRepository.findAllCurrentOwnerBookings(ownerId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookingStream = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(ownerId, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookingStream = bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, Status.WAITING, sort);
                break;
            case APPROVED:
                bookingStream = bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, Status.APPROVED, sort);
                break;
            case REJECTED:
                bookingStream = bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, Status.REJECTED, sort);
                break;
            default:
                throw new NotYetImplementedException();

        }
        return bookingStream.map(BookingMapper::toBookingDto).collect(Collectors.toUnmodifiableList());
    }

    private void validation(CreationBooking creationBooking) {
       List<String> mistakes = new ArrayList<>();

        validator.validate(creationBooking).forEach(mistake -> {
            String message = mistake.getPropertyPath() + ": " + mistake.getMessage();
            mistakes.add(message);
        });

        if (!mistakes.isEmpty()) {
            throw new ValidationException("Ошибки: " + mistakes);
        }
    }

}
