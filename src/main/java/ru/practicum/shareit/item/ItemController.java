package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    @Autowired
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public Item create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи");
        return itemService.createItem(userId, itemDto);

    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId,
                          @Valid @RequestBody ItemDto item) {
        log.info("Получен запрос на изменение данных вещи");
        return itemService.updateItem(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto find(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Получен запрос на получение данных о вещи");
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на вывод данных о всех вещах");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text", required = true) String text) {
        log.info("Получен запрос на поиск вещи");
        return itemService.searchItem(text.toLowerCase());
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                    @Valid @RequestBody CommentDto commentDTO) {
        log.info("Получен запрос на добавление вещи");
        return commentService.createComment(userId, itemId, commentDTO);
    }
}
