package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;


    @Override
    public Item createItem(Long userId, ItemDto itemDto) throws ValidationException, NotFoundException {
        if (userId <= 0) {
            throw new ValidationException("Не найден пользователь при добавлении вещи");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ValidationException("Не найден пользователь при добавлении вещи"));

        Item item = ItemMapper.fromItemDto(itemDto);

        if (item.getName() == null || item.getName().isBlank() || item.getDescription() == null || item.getDescription().isBlank() || !item.getAvailable()) {
            throw new ValidationException("Некорректный запрос при создании вещи");
        }

        item.setOwner(user);
        return itemRepository.save(item);
    }


    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) throws NotFoundException {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isPresent()) {
            Item foundItem = item.get();
            if (foundItem.getOwner().getId() == userId) {
                if (itemDto.getName() != null) {
                    foundItem.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null) {
                    foundItem.setDescription(itemDto.getDescription());
                }
                if (itemDto.getAvailable() != null) {
                    foundItem.setAvailable(itemDto.getAvailable());
                }
                itemRepository.save(foundItem);
                return ItemMapper.toItemDto(foundItem);
            } else throw new NotFoundException("Не найден такой владелец вещи");
        } else return null;
    }

    @Override
    public ItemWithBookingDto getItem(Long userId, Long id) throws NotFoundException {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException(""));
        ItemWithBookingDto itemDTO = ItemMapper.toItemWithBookingDTO(item);

        if (item.getOwner().getId() != userId) {
            itemDTO.setLastBooking(null);
            itemDTO.setNextBooking(null);
            return itemDTO;
        }


        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime currentDateTimeUTC = currentDateTime.atOffset(ZoneOffset.UTC).toLocalDateTime();

        List<Booking> lastBookings = bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(id, currentDateTimeUTC);
        if (!lastBookings.isEmpty()) {
            itemDTO.setLastBooking(BookingMapper.toBookingDto(lastBookings.get(0)));
        } else {
            itemDTO.setLastBooking(null);
        }

        List<Booking> nextBookings = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(id, currentDateTimeUTC);
        if (!nextBookings.isEmpty()) {
            itemDTO.setNextBooking(BookingMapper.toBookingDto(nextBookings.get(0)));
        } else {
            itemDTO.setNextBooking(null);
        }

        return itemDTO;
    }


    @Override
    public List<ItemWithBookingDto> getAllItems(Long userId) {
        List<ItemWithBookingDto> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(ItemMapper::toItemWithBookingDTO)
                .collect(Collectors.toList());
        for (ItemWithBookingDto item : items) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime currentDateTimeUTC = currentDateTime.atOffset(ZoneOffset.UTC).toLocalDateTime();
            List<Booking> bookings = bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(item.getId(), currentDateTimeUTC);
            if (item.getLastBooking() == null) {
                item.setNextBooking(null);
            }
            if (!bookings.isEmpty()) {
                item.setLastBooking(BookingMapper.toBookingDto(bookings.get(0)));
            }
            bookings = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(item.getId(), currentDateTimeUTC);
            if (!bookings.isEmpty()) {
                item.setNextBooking(BookingMapper.toBookingDto(bookings.get(0)));
            }
        }
        return items;
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<ItemDto>();
        } else return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


}
