package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.Status.REJECTED;
import static ru.practicum.shareit.booking.Status.WAITING;

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
        validate(creationBooking);
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


    private void validate(CreationBooking creationBooking) {
        List<String> mistakes = new ArrayList<>();

        validator.validate(creationBooking).forEach(mistake -> {
            String message = mistake.getPropertyPath() + ": " + mistake.getMessage();
            mistakes.add(message);
        });

        if (!mistakes.isEmpty()) {
            throw new ValidationException("Ошибки: " + mistakes);
        }
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
    public List<BookingDto> getAllBookingsByBooker(long bookerId, State state, int from, int size) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("объект Booker не найден в репозитории");
        }
        Stream<Booking> bookingStream;
        final Sort sort = Sort.sort(Booking.class).by(Booking::getStart).descending();
        final Pageable page = PageRequest.of(from / size, size, sort);
        switch (state) {
            case ALL:
                bookingStream = bookingRepository.findAllByBookerId(bookerId, page);
                break;
            case PAST:
                bookingStream = bookingRepository.findAllByBookerIdAndEndIsBefore(bookerId, LocalDateTime.now(), page);
                break;
            case CURRENT:
                bookingStream = bookingRepository.findAllCurrentBookerBookings(bookerId, LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookingStream = bookingRepository.findAllByBookerIdAndStartIsAfter(bookerId, LocalDateTime.now(), page);
                break;
            case WAITING:
                bookingStream = bookingRepository.findAllByBookerIdAndStatusIs(bookerId, Status.WAITING, page);
                break;
            case APPROVED:
                bookingStream = bookingRepository.findAllByBookerIdAndStatusIs(bookerId, Status.APPROVED, page);
                break;
            case REJECTED:
                bookingStream = bookingRepository.findAllByBookerIdAndStatusIs(bookerId, Status.REJECTED, page);
                break;
            default:
                throw new NotYetImplementedException();

        }
        return bookingStream.map(BookingMapper::toBookingDto).collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getAllBookingsByOwner(long ownerId, State state, int from, int size) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("объект Владелец  не найден в репозитории");
        }
        Stream<Booking> bookingStream;
        final Sort sort = Sort.sort(Booking.class).by(Booking::getStart).descending();
        final Pageable page = PageRequest.of(from / size, size, sort);
        switch (state) {
            case ALL:
                bookingStream = bookingRepository.findAllByItemOwnerId(ownerId, page);
                break;
            case PAST:
                bookingStream = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(ownerId, LocalDateTime.now(), page);
                break;
            case CURRENT:
                bookingStream = bookingRepository.findAllCurrentOwnerBookings(ownerId, LocalDateTime.now(), page);
                break;
            case FUTURE:
                bookingStream = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(ownerId, LocalDateTime.now(), page);
                break;
            case WAITING:
                bookingStream = bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, Status.WAITING, page);
                break;
            case APPROVED:
                bookingStream = bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, Status.APPROVED, page);
                break;
            case REJECTED:
                bookingStream = bookingRepository.findAllByItemOwnerIdAndStatusIs(ownerId, Status.REJECTED, page);
                break;
            default:
                throw new NotYetImplementedException();

        }
        return bookingStream.map(BookingMapper::toBookingDto).collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    @Override
    public BookingDto confirmationBooking(long bookingId, long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("объект Booking не найден в репозитории"));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new NotFoundException("Только владелец может подтвердить бронирование");
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new ValidationException("Объект Booking имеет отличный статус от WAITING");
        }
        booking.setStatus(approved ? APPROVED : REJECTED);
        return BookingMapper.toBookingDto(booking);
    }
}
