package ru.practicum.shareit.item;

import lombok.SneakyThrows;
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
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @Autowired
    public ItemController(ItemService itemService, CommentService commentService) {
        this.itemService = itemService;
        this.commentService = commentService;
    }


    @PostMapping
    public Item create(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на добавление вещи");
        return itemService.create(userId, itemDto);

    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                          @Valid @RequestBody ItemDto item) {
        log.info("Получен запрос на изменение данных вещи");
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto find(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Получен запрос на получение данных о вещи");
        return itemService.find(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingDto> findAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен запрос на вывод данных о всех вещах");
        return itemService.findAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(name = "text", required = true) String text) {
        log.info("Получен запрос на поиск вещи");
        return itemService.search(text.toLowerCase());
    }


    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId,
                                    @Valid @RequestBody CommentDto commentDTO) {
        log.info("Получен запрос на добавление вещи");
        return commentService.createComment(userId, itemId, commentDTO);
    }
}
