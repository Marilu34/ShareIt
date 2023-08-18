package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestList;
import ru.practicum.shareit.request.dto.ShortRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public RequestDto createRequests(@RequestHeader("X-Sharer-User-id") long requesterId,
                                     @RequestBody ShortRequestDto itemRequestDto) {

        itemRequestDto.setRequesterId(requesterId);
        RequestDto created = itemRequestService.createRequests(itemRequestDto);
        log.info("Создан новый запрос {}", created);
        return created;
    }

    @GetMapping("/{requestId}")
    public RequestList getRequestById(@RequestHeader("X-Sharer-User-id") long userId,
                                      @PathVariable long requestId) {
        RequestList result = itemRequestService.getRequestById(userId, requestId);
        log.info("Получен для Пользователя {} слеуюдщие запросы {}", userId, requestId);
        return result;
    }

    @GetMapping("/all")
    public List<RequestList> getAllRequests(@RequestHeader("X-Sharer-User-id") long userId,
                                            @RequestParam(defaultValue = "0")
                                            @PositiveOrZero(message = "from должен быть больше нуля") int from,
                                            @RequestParam(defaultValue = "10")
                                            @Positive(message = "size должен быть больше нуля") int size) {
        List<RequestList> result = itemRequestService.getAllRequests(userId, from, size);
        log.info("Получены для Пользователя {} следующие запросы {} от других Пользователей. Запросы от {}, размер {} ",
                userId, result.size(), from, size);
        return result;
    }


    @GetMapping
    public List<RequestList> getAllRequestsBySearcher(@RequestHeader("X-Sharer-User-id") long requesterId) {
        List<RequestList> result = itemRequestService.getAllRequestsBySearcher(requesterId);
        log.info("Получены {} запросы от Пользователя {}", result.size(), requesterId);
        return result;
    }
}
