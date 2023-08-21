package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemAccessDeniedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.APPROVED;


@Component
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;


    public Item getById(long itemId) throws NotFoundException {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Вещь " + itemId + " не найдена");
        } else {
            return optionalItem.get();
        }
    }


    public Item getById(long itemId, long ownerId) {
        Item item = getById(itemId);
        if (item.getOwnerId() == ownerId) {
            updateItemsWithBookings(List.of(item));
        }
        item.setComments(commentRepository.findByItem_idOrderByCreatedDesc(item.getId()));
        return item;
    }


    public List<Item> getOwnedItemsList(long ownerId, int from, int size) {
        Pageable page =  PageRequest.of(from / size, size);
        List<Item> itemList = itemRepository.findByOwnerIdOrderById(ownerId, page);
        updateItemsWithBookings(itemList);
        updateItemsWithComments(itemList);

        return itemList;
    }

    private void updateItemsWithComments(List<Item> itemList) {
        if (!itemList.isEmpty()) {
            List<Long> itemIdList = itemList.stream().map(Item::getId).collect(Collectors.toList());
            Map<Long, Item> itemMap = itemList.stream().collect(Collectors.toMap(Item::getId, item -> item));

            List<Comment> commentsList = commentRepository.findByItem_idInOrderByCreatedDesc(itemIdList);
            for (Comment comment : commentsList) {
                Item item = itemMap.get(comment.getItem().getId());

                item.getComments().add(comment);
            }
        }
    }


    private void updateItemsWithBookings(List<Item> itemList) {
        if (!itemList.isEmpty()) {
            LocalDateTime nowDateTime = LocalDateTime.now();
            List<Long> itemIdList = itemList.stream().map(Item::getId).collect(Collectors.toList());
            Map<Long, Item> itemMap = itemList.stream().collect(Collectors.toMap(Item::getId, item -> item));

            List<Booking> bookingsList = bookingRepository.findByItem_idInAndStatus(itemIdList, APPROVED);
            for (Booking booking : bookingsList) {
                Item item = itemMap.get(booking.getItem().getId());

                Booking lastBooking = item.getLastBooking();
                if (booking.getRentStartDate().isBefore(nowDateTime)
                        && (lastBooking == null
                        || lastBooking.getRentStartDate().isBefore(booking.getRentStartDate()))) {
                    item.setLastBooking(booking);
                }
                Booking nextBooking = item.getNextBooking();
                if (booking.getRentStartDate().isAfter(nowDateTime)
                        && (nextBooking == null
                        || nextBooking.getRentStartDate().isAfter(booking.getRentStartDate()))) {
                    item.setNextBooking(booking);
                }
            }
        }
    }


    public Item create(ItemCreateRequest itemCreateRequest, long ownerId) {
        userService.getById(ownerId);

        Item item = ItemDtoMapper.toItem(itemCreateRequest);
        item.setOwnerId(ownerId);

        long requestId = itemCreateRequest.getRequestId();
        if (requestId > 0) {
            item.setItemRequest(itemRequestRepository.getReferenceById(requestId));
        }

        return itemRepository.save(item);
    }


    public Item update(Item item, long ownerId) {
        if (item != null && item.getId() > 0) {
            Item storageItem = getById(item.getId());
            if (storageItem.getOwnerId() == ownerId) {
                if (item.getName() != null && !item.getName().isBlank()) {
                    storageItem.setName(item.getName());
                }
                if (item.getDescription() != null && !item.getDescription().isBlank()) {
                    storageItem.setDescription(item.getDescription());
                }
                if (item.getAvailable() != null) {
                    storageItem.setAvailable(item.getAvailable());
                }

                return itemRepository.save(storageItem);
            } else {
                throw new ItemAccessDeniedException("Вещь принадлежит другому пользователю");
            }
        }
        return null;
    }


    public void delete(long itemId, long ownerId) {
        Item savedItem = getById(itemId);
        if (savedItem.getOwnerId() != ownerId) {
            throw new ItemAccessDeniedException("Вещь принадлежит другому пользователю");
        }
        itemRepository.delete(savedItem);
    }

    public List<Item> search(String text, int from, int size) {
        if (!text.isBlank()) {
            Pageable page =  PageRequest.of(from / size, size);
            List<Item> searchResults = itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text, text, true, page);
            return searchResults;
        } else {
            return List.of();
        }
    }

    public Comment createComment(String text, long itemId, long authorId) throws ValidationException {
        Item item = getById(itemId);
        User author = userService.getById(authorId);

        if (authorHaveBookingsOfItem(itemId, authorId)) {
            Comment comment = new Comment(0, text, item, author, LocalDateTime.now());
            return commentRepository.save(comment);
        } else {
            throw new ValidationException("Пользователь " + authorId + " не имеет завершенных бронирований вещи " + itemId);
        }
    }

    private boolean authorHaveBookingsOfItem(long itemId, long authorId) {
        List<Booking> bookings = bookingRepository.findByItem_idAndBooker_idAndStatusAndRentEndDateIsBefore(itemId, authorId, APPROVED, LocalDateTime.now());
        return !bookings.isEmpty();
    }
}