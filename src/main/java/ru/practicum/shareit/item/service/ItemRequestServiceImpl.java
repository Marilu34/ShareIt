package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemRequestMapper;
import ru.practicum.shareit.item.ItemRequestRepository;
import ru.practicum.shareit.item.dto.AddItemRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Validator validator;
    private final Sort sort = Sort.by("created");

    @Transactional
    @Override
    public ItemRequestDto create(AddItemRequestDto itemRequestDto) {
        validate(itemRequestDto);
        User requester = userRepository.findById(itemRequestDto.getRequesterId())
                .orElseThrow(() -> new NotFoundException("Объект Пользователь не найден в репозитории"));
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto, requester);
        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestWithItemsDto> findAllRequesterRequests(long requesterId) {
        ifUserNotExistsThenThrowUserNotFoundException(requesterId);
        List<ItemRequest> dtoList = itemRequestRepository.findAllByRequesterId(requesterId, sort);
        return dtoList.stream()
                .map(o -> ItemRequestMapper.mapToItemRequestWithItemsDto(o, itemRepository.findAllByRequestId(o.getId())))
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestWithItemsDto> findAllPageable(long userId, int from, int size) {
        ifUserNotExistsThenThrowUserNotFoundException(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.Direction.DESC, "created");
        Page<ItemRequest> page = itemRequestRepository.findAllByRequesterIdNot(userId, pageRequest);
        return page.get()
                .map(ir -> ItemRequestMapper.mapToItemRequestWithItemsDto(ir,
                        itemRepository.findAllByRequestId(ir.getId()))).collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestWithItemsDto getRequestById(long userId, long requestId) {
        ifUserNotExistsThenThrowUserNotFoundException(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на поиск Item не найден"));

        return ItemRequestMapper.mapToItemRequestWithItemsDto(request, itemRepository.findAllByRequestId(requestId));
    }

    private void ifUserNotExistsThenThrowUserNotFoundException(long userId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException("Объект Пользователь не найден");
    }

    private void validate(AddItemRequestDto itemRequestDto) {
        Set<ConstraintViolation<AddItemRequestDto>> violations = validator.validate(itemRequestDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
