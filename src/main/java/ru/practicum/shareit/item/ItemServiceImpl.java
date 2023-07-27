package ru.practicum.shareit.item;

import org.springframework.data.crossstore.ChangeSetPersister;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }


    @Override
    public Item create(long userId, ItemDto itemDto) throws ValidationException, NotFoundException {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ValidationException("Не найден пользователь при добавлении вещи"));
        Item item = ItemMapper.fromItemDto(itemDto);

        if (item.getName() == null || item.getName().isBlank() || item.getName().equals("") ||
                item.getDescription() == null || item.getDescription().isBlank()
                || item.getDescription().equals("") || !item.getAvailable()) {
            throw new NotFoundException("Некорректный запрос при создании вещи");
        }
        item.setOwner(user);
        return itemRepository.save(item);
    }


    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) throws NotFoundException {
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
    public ItemWithBookingDto find(long userId, long id) throws NotFoundException {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException(""));
        ItemWithBookingDto itemDTO = ItemMapper.toItemWithBookingDTO(item);
        if (item.getOwner().getId() == userId) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> lastBookings = bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(id, now);
            if (!lastBookings.isEmpty()) {
                itemDTO.setLastBooking(BookingMapper.toBookingDto(lastBookings.get(0)));
            }
            List<Booking> nextBookings = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(id, now);
            if (!nextBookings.isEmpty()) {
                itemDTO.setNextBooking(BookingMapper.toBookingDto(nextBookings.get(0)));
            }
        }
        return itemDTO;
    }

    @Override
    public List<ItemWithBookingDto> findAll(long userId) {
        List<ItemWithBookingDto> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(ItemMapper::toItemWithBookingDTO)
                .collect(Collectors.toList());
        for (ItemWithBookingDto item : items) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = bookingRepository.findByItemIdAndEndBeforeOrderByEndDesc(item.getId(), now);
            if (!bookings.isEmpty()) {
                item.setLastBooking(BookingMapper.toBookingDto(bookings.get(0)));
            }
            bookings = bookingRepository.findByItemIdAndStartAfterOrderByStartAsc(item.getId(), now);
            if (!bookings.isEmpty()) {
                item.setNextBooking(BookingMapper.toBookingDto(bookings.get(0)));
            }
        }
        return items;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<ItemDto>();
        } else return itemRepository.search(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }


}
