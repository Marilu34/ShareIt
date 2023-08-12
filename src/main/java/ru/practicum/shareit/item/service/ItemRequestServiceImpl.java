package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemRequestMapper;
import ru.practicum.shareit.item.ItemRequestRepository;
import ru.practicum.shareit.item.dto.ShortRequestDto;
import ru.practicum.shareit.item.dto.RequestDto;
import ru.practicum.shareit.item.itemBooking.dto.RequestList;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Validator validator;
    private final Sort sort = Sort.by("created");

    private void validate(ShortRequestDto itemRequestDto) {
        List<String> mistakes = new ArrayList<>();

        validator.validate(itemRequestDto).forEach(mistake -> {
            String message = mistake.getPropertyPath() + ": " + mistake.getMessage();
            mistakes.add(message);
        });

        if (!mistakes.isEmpty()) {
            throw new ValidationException("Ошибки: " + mistakes);
        }
    }

    @Transactional
    @Override
    public RequestDto createRequests(ShortRequestDto itemRequestDto) {
        validate(itemRequestDto);
        User requester = userRepository.findById(itemRequestDto.getRequesterId())
                .orElseThrow(() -> new NotFoundException("Объект Пользователь не найден в репозитории"));
        ItemRequest itemRequest = ItemRequestMapper.toShortRequestDto(itemRequestDto, requester);
        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toRequestDto(itemRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestList> getAllRequestsBySearcher(long userId) {
        checkUser(userId);
        List<ItemRequest> itemsList = itemRequestRepository.findAllByRequesterId(userId, sort);
        return itemsList.stream()
                .map(itemRequest -> ItemRequestMapper.toRequestList(itemRequest, itemRepository.findAllByRequestId(itemRequest.getId())))
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestList> getAllRequests(long userId, int from, int size) {
        checkUser(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.Direction.DESC, "created");
        Page<ItemRequest> page = itemRequestRepository.findAllByRequesterIdNot(userId, pageRequest);
        return page.get()
                .map(ir -> ItemRequestMapper.toRequestList(ir,
                        itemRepository.findAllByRequestId(ir.getId()))).collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    @Override
    public RequestList getRequestById(long userId, long requestId) {
        checkUser(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на поиск Item не найден"));

        return ItemRequestMapper.toRequestList(request, itemRepository.findAllByRequestId(requestId));
    }

    private void checkUser(long userId) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("Объект Пользователь не найден");
    }

}
