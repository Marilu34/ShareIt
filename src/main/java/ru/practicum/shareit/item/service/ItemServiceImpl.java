package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.item.ItemRequestRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemBooking.ItemCommentsDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final Validator validator;

    private final ItemRequestRepository requestRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        validate(itemDto);
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("объект Owner не найден в репозитории"));
        ItemRequest request;
        if (itemDto.getRequestId() == null) {
            request = null;
        } else {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(" Не найден Item"));
        }
        Item item = ItemMapper.fromItemDto(itemDto, owner, request);
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
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

        validate(ItemMapper.toItemDto(item));
        item = itemRepository.save(item);
        return ItemMapper.toItemDto(item);
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

        return ItemMapper.toItemCommentDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public ItemDto getByItemId(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найден Item"));

        return ItemMapper.toItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemBookingsDto> getItemsByUserId(Long userId, int from, int size) {
        // Проверяем, существует ли пользователь
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Объект Пользователь не найден в репозитории");
        }

        PageRequest page = PageRequest.of(from / size, size);
        Page<Item> itemPage = itemRepository.findAllByOwnerId(userId, page);
        LocalDateTime now = LocalDateTime.now();

        return itemPage.stream()
                .map(item -> {
                    long itemId = item.getId();
                    ShortBookingDto lastBooking = bookingRepository
                            .findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(itemId, now, Status.APPROVED);

                    ShortBookingDto nextBooking = bookingRepository
                            .findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(itemId, now, Status.REJECTED);

                    return ItemMapper.toItemBookingsDto(item, lastBooking, nextBooking);
                })
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    @Override
    public Collection<ItemDto> getItemByComment(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findAllByAvailableTrueAndNameContainsOrDescriptionContainsAllIgnoreCase(text, Pageable.ofSize(100))
                .map(ItemMapper::toItemDto)
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
        Comment comment = CommentMapper.toComment(text,
                booking.getBooker(),
                booking.getItem());

        comment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }
    @Override
    public Collection<ItemDto> findByText(String text, int from, int size) {
        if (text.isBlank()) {
            return List.of();
        }
        PageRequest page = PageRequest.of(from / size, size);
        return itemRepository.findAllByAvailableTrueAndNameContainsOrDescriptionContainsAllIgnoreCase(text, page)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toUnmodifiableList());
    }

    private Item getItemById(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("объект Item не найден"));
    }

}
