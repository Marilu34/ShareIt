package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Constants;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestCreateRequest itemRequestCreateRequest,
                                                @Positive @RequestHeader(value = Constants.X_HEADER_NAME) long requestAuthorId) {
        log.info("Create item request{}, owner {}", itemRequestCreateRequest, requestAuthorId);
        return requestClient.createRequest(requestAuthorId, itemRequestCreateRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnItemRequests(@Positive @RequestHeader(value = Constants.X_HEADER_NAME) long requestAuthorId) {
        log.info("Get own item requests authorId {}", requestAuthorId);
        return requestClient.getOwnItemRequests(requestAuthorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Positive @RequestParam(defaultValue = "20") int size,
                                                 @Positive @RequestHeader(value = Constants.X_HEADER_NAME) long requestAuthorId) {
        log.info("Get all item requests from {} size {} requestAuthorId {}", from, size, requestAuthorId);
        return requestClient.getAllRequests(requestAuthorId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@Positive @PathVariable int requestId,
                                                 @Positive @RequestHeader(value = Constants.X_HEADER_NAME) long userId) {
        log.info("Get requestId {} for userId {}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }
}