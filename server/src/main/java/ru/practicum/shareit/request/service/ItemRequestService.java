package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;

    public ItemRequest create(ItemRequest itemRequest, long ownerId) {
        itemRequest.setRequestAuthor(userService.getById(ownerId));
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    public ItemRequest getById(long requestId, long userId) {
        userService.getById(userId);
        Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(requestId);
        if (optionalItemRequest.isEmpty()) {
            throw new NotFoundException("Запрос на вещь " + requestId + " не найден");
        } else {
            return optionalItemRequest.get();
        }
    }

    public List<ItemRequest> getOwnItemRequests(long requestAuthorId) {
        userService.getById(requestAuthorId);
        return itemRequestRepository.findByRequestAuthor_idOrderByCreatedDesc(requestAuthorId);
    }

    public List<ItemRequest> getAll(int from, int size, long requestAuthorId) {
        Pageable page = PageRequest.of(from / size, size);
        Page<ItemRequest> itemRequestPage = itemRequestRepository.findByRequestAuthor_idNotOrderByCreatedDesc(requestAuthorId, page);
        return itemRequestPage.getContent();
    }
}