package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.Status.*;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;



    @Override
    public Booking create(long userId, BookingDto bookingDto) throws Exception {
        Booking booking = BookingMapper.fromBookingDto(bookingDto);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException(""));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(""));
        if (item.getAvailable()) {
            booking.setItem(item);
            LocalDateTime now = LocalDateTime.now();
            if (item.getOwner().getId() != userId) {
               if( bookingDto.getStart()== null || bookingDto.getEnd()== null){
                   throw new ValidationException("Время старта или окончания не может равняться нулю");
               }
                if (bookingDto.getStart().isAfter(now) && bookingDto.getEnd().isAfter(now)
                        && bookingDto.getEnd().isAfter(bookingDto.getStart())) {
                    booking.setBooker(user);
                    booking.setStatus(WAITING);
                    return bookingRepository.save(booking);
                }
                throw new ValidationException("Некорректный запрос при бронировании вещи");
            }
            throw new NotFoundException("Не найден пользователь");
        }
        throw new ValidationException("Некорректный запрос при бронировании вещи");
    }


    @Override
    public Booking confirmationOrRejection(long userId, long bookingId, Boolean approved) throws ValidationException, NotFoundException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(""));
        if (itemRepository.getReferenceById(booking.getItem().getId()).getOwner().getId() == userId) {
            if (booking.getStatus() == WAITING) {
                if (approved) {
                    booking.setStatus(APPROVED);
                } else booking.setStatus(REJECTED);
                bookingRepository.save(booking);
                return booking;
            } else throw new ValidationException("Некорректный запрос ");
        } else throw new NotFoundException("Не найдено");

    }


    @Override
    public Booking find(long userId, long bookingId) throws NotFoundException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Не найдено"));
        if (booking.getBooker().getId() == userId ||
                itemRepository.getReferenceById(booking.getItem().getId()).getOwner().getId() == userId) {
            return booking;
        } else throw new NotFoundException("Не найдено");
    }

    @Override
    public List<Booking> findAll(long userId, State state) throws NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найдено"));
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, REJECTED);
                break;
        }
        return bookings;
    }

    @Override
    public List<Booking> allUserItems(long userId, State state) throws NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не найдено"));
        List<Booking> bookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItem_OwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        now, now);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_OwnerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_OwnerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_OwnerIdAndStatusOrderByStartDesc(userId, WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_OwnerIdAndStatusOrderByStartDesc(userId, REJECTED);
                break;
        }
        return bookings;

    }
}
