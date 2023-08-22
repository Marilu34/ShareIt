package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Constants;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestBody ItemCreateRequest itemCreateRequest,
                          @RequestHeader(value = Constants.X_HEADER_NAME) int ownerId) {
        log.info("Create item, owner {}: " + itemCreateRequest.toString(), ownerId);
        return ItemDtoMapper.toItemDto(itemService.create(itemCreateRequest, ownerId));
    }


    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@PathVariable int itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(value = Constants.X_HEADER_NAME) int ownerId) {
        log.info("Update item {}, ownerId {}: " + itemDto, itemId, ownerId);

        itemDto.setId(itemId);
        Item item = ItemDtoMapper.toItem(itemDto);

        return ItemDtoMapper.toItemDto(itemService.update(item, ownerId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getOwnedItemsList(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "20") int size,
                                           @RequestHeader(value = Constants.X_HEADER_NAME) int ownerId) {
        log.info("Get owned items list, ownerId {}", ownerId);
        return ItemDtoMapper.toItemDtoList(itemService.getOwnedItemsList(ownerId, from, size));
    }


    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto get(@PathVariable int itemId,
                       @RequestHeader(value = Constants.X_HEADER_NAME) int ownerId) {
        log.info("Get itemId {}", itemId);
        return ItemDtoMapper.toItemDto(itemService.getById(itemId, ownerId));
    }


    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> search(@RequestParam(defaultValue = "0") int from,
                                @RequestParam(defaultValue = "20") int size,
                                @RequestParam(defaultValue = "") String text) {
        log.info("Search text '{}'", text);
        if (!text.isBlank()) {
            return ItemDtoMapper.toItemDtoList(itemService.search(text, from, size));
        } else {
            return List.of();
        }
    }


    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable int itemId,
                       @RequestHeader(value = Constants.X_HEADER_NAME) int ownerId) {
        log.info("Delete itemId {}, ownerId {}", itemId, ownerId);
        itemService.delete(itemId, ownerId);
    }


    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto,
                                    @PathVariable int itemId,
                                    @RequestHeader(value = Constants.X_HEADER_NAME) int authorId) {
        log.info("Create comment for item {}, author {}: " + commentDto.toString(), itemId, authorId);
        return CommentDtoMapper.toCommentDto(itemService.createComment(commentDto.getText(), itemId, authorId));
    }
}