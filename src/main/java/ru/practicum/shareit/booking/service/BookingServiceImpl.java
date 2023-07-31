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
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.LocalDateTime;
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
    public BookingDto create(BookingCreationDto bookingCreationDto) {
        validate(bookingCreationDto);
        Item item = itemRepository.findById(bookingCreationDto.getItemId())
                .orElseThrow(() -> new NotFoundException("bookingCreationDto.getItemId()"));
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException(bookingCreationDto.getItemId());
        }
        //TODO not logged
        if (item.getOwner().getId() == bookingCreationDto.getBookerId()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner cannot book own item");
        }
        User booker = userRepository.findById(bookingCreationDto.getBookerId())
                .orElseThrow(() -> new NotFoundException("bookingCreationDto.getBookerId()"));
        Booking booking = bookingRepository.save(BookingMapper.mapCreationDtoToBooking(bookingCreationDto, item, booker));

        return BookingMapper.mapBookingToDto(booking);
    }

    @Transactional
    @Override
    public BookingDto ownerAcceptation(long bookingId, long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("bookingId"));

        //TODO Данные ошибки не логируются
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Only owner allowed operation");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking has status different from WAITING");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.mapBookingToDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDto findBookingByOwnerOrBooker(long bookingId, long userId) {
        Booking booking = bookingRepository.findBookingByOwnerOrBooker(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("bookingId"));
        return BookingMapper.mapBookingToDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllBookingsOfBooker(long bookerId, State state) {
        if (!userRepository.existsById(bookerId)) {
            throw new NotFoundException("bookerId");
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
        return bookingStream.map(BookingMapper::mapBookingToDto).collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> findAllBookingsOfOwner(long ownerId, State state) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("ownerId");
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
        return bookingStream.map(BookingMapper::mapBookingToDto).collect(Collectors.toUnmodifiableList());
    }

    private void validate(BookingCreationDto bookingCreationDto) {
        Set<ConstraintViolation<BookingCreationDto>> violations = validator.validate(bookingCreationDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
