package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemBooking.ItemCommentsDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final Validator validator;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        validate(itemDto);
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("объект Owner не найден в репозитории"));
        Item item = ItemMapper.mapToItem(itemDto, owner);
        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    private void validate(ItemDto itemDto) {
        List<String> mistakes = new ArrayList<>();

        validator.validate(itemDto).forEach(mistake -> {
            String message = mistake.getPropertyPath() + ": " + mistake.getMessage();
            mistakes.add(message);
        });

        if (!mistakes.isEmpty()) {
            throw new ValidationException("Ошибки: " + mistakes);
        }
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto) {
        Item item = getItemById(itemDto.getId());

        if (userId != item.getOwner().getId()) {
            throw new IllegalStateException("Только владелец может обновить данные о Вещи");
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }

        validate(ItemMapper.mapToItemDto(item));
        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }


    @Override
    public ItemCommentsDto getByItemId(Long itemId, Long requestFromUserId) {
        Item item = getItemById(itemId);
        ShortBookingDto lastBooking = null;
        ShortBookingDto nextBooking = null;
        if (item.getOwner().getId() == requestFromUserId) {
            LocalDateTime now = LocalDateTime.now();
            lastBooking = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemId, now, Status.APPROVED);
            nextBooking = bookingRepository.findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(itemId, now, Status.REJECTED);
        }

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId);

        return ItemMapper.mapToItemWithBookingsAndCommentsDto(item, lastBooking, nextBooking, comments);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemBookingsDto> getItemsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("объект Пользователь не найден в репозитории");
        }

        Collection<Item> items = itemRepository.findAllByOwnerId(userId).collect(Collectors.toUnmodifiableList());
        Collection<ItemBookingsDto> itemBookingsDtos = new ArrayList<>(items.size());
        LocalDateTime now = LocalDateTime.now();
        for (Item item : items) {
            long itemId = item.getId();
            ShortBookingDto lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemId, now, Status.APPROVED);
            ShortBookingDto nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(itemId, now, Status.REJECTED);
            itemBookingsDtos.add(ItemMapper.mapToItemWithBookingsDto(item, lastBooking, nextBooking));
        }

        return itemBookingsDtos;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> getItemByComment(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAllByAvailableTrueAndNameContainsOrDescriptionContainsAllIgnoreCase(text)
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public CommentDto postComment(String text, Long itemId, Long authorId) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> items = bookingRepository
                .findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(itemId, authorId, Status.APPROVED, now);
        if (items.isEmpty()) {
            throw new ValidationException("объект Item не найден в репозитории");
        }
        Booking booking = items.get(0);
        Comment comment = CommentMapper.mapToComment(text,
                booking.getBooker(),
                booking.getItem());

        comment = commentRepository.save(comment);
        return CommentMapper.mapToDto(comment);
    }


    private Item getItemById(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("объект Item не найден"));
    }

}
