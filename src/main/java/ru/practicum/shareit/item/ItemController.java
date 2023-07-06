package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemController {

   private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") @Min(1) Long userId,
                                              @RequestBody ItemDto itemDto,
                                              @PathVariable Long itemId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.updateItem(itemId, userId, itemDto));
    }

    @GetMapping("{itemId}")
    public ResponseEntity<ItemDto> getItemByItemId(@PathVariable Long itemId) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.getItemByItemId(itemId));
    }

    @GetMapping
    public ResponseEntity<ItemDto> getPersonalItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getPersonalItems(userId));
    }

    @GetMapping("search")
    public ResponseEntity<ItemDto> getFoundItems(@RequestParam String text) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(itemService.getFoundItems(text));
    }

}

