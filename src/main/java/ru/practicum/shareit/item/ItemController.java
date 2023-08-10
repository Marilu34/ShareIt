package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemBooking.ItemCommentsDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto = itemService.createItem(userId, itemDto);
        log.info("Пользователь {} создал Вещь {}", userId, itemDto);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto.setId(itemId);
        itemDto = itemService.updateItem(userId, itemDto);
        log.info("Пользователь {} обновил Вещь {}", userId, itemDto);
        return itemDto;
    }

    @GetMapping("/{itemId}")
    public ItemCommentsDto getByItemId(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemCommentsDto itemDto = itemService.getByItemId(itemId,userId);
        log.info("Получен Item по ID {}", itemDto);
        return itemDto;
    }

    @GetMapping
    public Collection<ItemBookingsDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero(message = "from cannot be negative") int from,
                                                        @RequestParam(defaultValue = "10") @Positive(message = "size must be positive") int size
    ) {
        Collection<ItemBookingsDto> collection = itemService.getItemsByUserId(userId, from, size);
        log.info("Данные {} Вещи принадлежат пользователю {}", collection.size(), userId);
        return collection;
    }


    @GetMapping("/search")
    public Collection<ItemDto> getItemByComment(@RequestParam() String text) {
        Collection<ItemDto> collection = itemService.getItemByComment(text);
        log.info("Вещи {} по описанию {}", collection.size(), text);
        return collection;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader("X-Sharer-User-id") Long authorId,
                                  @PathVariable Long itemId,
                                  @RequestBody @Valid @NotBlank Map<String, String> requestBody) {
        if (!requestBody.containsKey("text") || requestBody.get("text").isBlank()) {
            throw new ValidationException("Ошибка с комментарием");
        }
        CommentDto commentDto = itemService.postComment(requestBody.get("text"), itemId, authorId);
        log.info("Пользователь {} для Вещи {} добавил комментарий", authorId, itemId);
        return commentDto;

    }

}
