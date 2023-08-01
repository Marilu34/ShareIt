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
    public ItemDto createNewItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto = itemService.createItem(userId, itemDto);
        log.info("Пользователь {} создал Вещь {}", userId, itemDto);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemFields(@RequestBody ItemDto itemDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemDto.setId(itemId);
        itemDto = itemService.updateItem(userId, itemDto);
        log.info("Пользователь {} обновил Вещь {}", userId, itemDto);
        return itemDto;
    }

    @GetMapping("/{itemId}")
    public ItemCommentsDto getByItemId(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemCommentsDto itemDto = itemService.getByItemId(itemId, userId);
        log.info("Пользователь {} имеет следующие Вещи {}", userId, itemDto);
        return itemDto;
    }

    @GetMapping
    public Collection<ItemBookingsDto> getAllUsersItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        Collection<ItemBookingsDto> collection = itemService.getItemsByUserId(userId);
        log.info("Данные {} Вещи принадлежат пользователю{}", collection.size(), userId);
        return collection;
    }

    @GetMapping("/search")
    public Collection<ItemDto> findByText(@RequestParam() String text) {
        Collection<ItemDto> collection = itemService.getItemByComment(text);
        log.info("It has been found {} items with text \"{}\"", collection.size(), text);
        return collection;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postCommentForItem(@RequestHeader("X-Sharer-User-id") Long authorId,
                                         @PathVariable Long itemId,
                                         @RequestBody @Valid @NotBlank Map<String, String> requestBody)  {
        if (!requestBody.containsKey("text") || requestBody.get("text").isBlank()) {
            throw new ValidationException("");
        }
        CommentDto commentDto = itemService.postCommentForItemFromAuthor(requestBody.get("text"), itemId, authorId);
        log.info("Author {} added comment for item {}", authorId, itemId);
        return commentDto;

    }

}
