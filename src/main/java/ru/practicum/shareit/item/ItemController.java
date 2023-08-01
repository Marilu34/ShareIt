package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private static final String OWNER = "X-Sharer-User-Id";
    private ItemService itemService;

    private UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }


    @ResponseBody
    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER) Long ownerId) {
        log.info("Вещь " + itemDto + " была создана Владельцем " + ownerId);
        ItemDto newItem = null;
        if (userService.ifUserExist(ownerId)) {
            newItem = itemService.create(itemDto, ownerId);
        }
        return newItem;
    }

    @GetMapping
    public List<ItemDto> getOwnersItems(@RequestHeader(OWNER) Long ownerId) {
        log.info("Получены все вещей владельца с id = " + ownerId);
        return itemService.getOwnersItems(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        log.info("Вещь с id =  " + itemId + " была получен");
        return itemService.getItemById(itemId);
    }

    @ResponseBody
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                              @RequestHeader(OWNER) Long ownerId) {
        log.info("Обновление вещи с id = " + itemId);
        ItemDto newItem = null;
        if (userService.ifUserExist(ownerId)) {
            newItem = itemService.updateItem(itemDto, ownerId, itemId);
        }
        return newItem;
    }

    @DeleteMapping("/{itemId}")
    public ItemDto deleteItem(@PathVariable Long itemId, @RequestHeader(OWNER) Long ownerId) {
        log.info("Удаление вещи с id =" + itemId);
        return itemService.deleteItem(itemId, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchQueryItem(@RequestParam String text) {
        log.info("Поиск вещи с описанием = {}", text);
        return itemService.searchQueryItem(text);
    }
}
