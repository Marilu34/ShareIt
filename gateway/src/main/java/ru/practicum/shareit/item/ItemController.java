package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Constants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemRequest itemRequest,
                                             @Positive @RequestHeader(value = Constants.X_HEADER_NAME) int ownerId) {
        log.info("Create item {}, owner {}", ownerId, itemRequest.toString());
        return itemClient.createItem(ownerId, itemRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@Positive @PathVariable int itemId,
                                             @Valid @RequestBody ItemDto itemDto,
                                             @Positive @RequestHeader(value = Constants.X_HEADER_NAME) int ownerId) {
        log.info("Update item {}, ownerId {}: ", itemDto, ownerId);
        return itemClient.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnedItemsList(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                    @Positive @RequestParam(defaultValue = "20") int size,
                                                    @Positive @RequestHeader(value = Constants.X_HEADER_NAME) long ownerId) {
        log.info("Get owned items list, ownerId {}, from {}, size {}", ownerId, from, size);
        return itemClient.getItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getSingleItem(@Positive @PathVariable int itemId,
                                                @Positive @RequestHeader(value = Constants.X_HEADER_NAME) int ownerId) {
        log.info("Get itemId {} by userId {}", itemId, ownerId);
        return itemClient.getSingleItem(ownerId, itemId);
    }


    @GetMapping("/search")
    public ResponseEntity<Object> search(@PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                         @Positive @RequestParam(defaultValue = "20") int size,
                                         @RequestParam(defaultValue = "") String text) {
        if (text.trim().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        log.info("Search text '{}', from {}, size {}", text, from, size);
        return itemClient.search(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@Positive @PathVariable int itemId,
                                             @Positive @RequestHeader(value = Constants.X_HEADER_NAME) int ownerId) {
        log.info("Delete itemId {}, ownerId {}", itemId, ownerId);
        return itemClient.deleteItem(itemId, ownerId);
    }


    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto commentDto,
                                                @Positive @PathVariable int itemId,
                                                @Positive @RequestHeader(value = Constants.X_HEADER_NAME) int authorId) {
        log.info("Create comment {}, itemId {}, authorId {}: ", commentDto, itemId, authorId);
        return itemClient.postComment(authorId, itemId, commentDto);
    }
}