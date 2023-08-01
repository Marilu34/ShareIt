package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private ItemStorage itemStorage;
    private ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(@Qualifier("InMemoryItemStorage") ItemStorage itemStorage, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        return itemMapper.toItemDto(itemStorage.createItem(itemMapper.toItem(itemDto, ownerId)));
    }


    @Override
    public ItemDto getItemById(Long id) {
        return itemMapper.toItemDto(itemStorage.getItemById(id));
    }


    @Override
    public List<ItemDto> getOwnersItems(Long ownerId) {
        List<Item> items = itemStorage.getItemsByOwner(ownerId);
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            itemDtos.add(itemDto);
        }

        return itemDtos;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long ownerId, Long itemId) {
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item item = itemStorage.getItemById(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException("Ошибка! У пользователя отсутсвует данная вещь");
        }
        return itemMapper.toItemDto(itemStorage.updateItem(itemMapper.toItem(itemDto, ownerId)));
    }


    @Override
    public List<ItemDto> searchQueryItem(String text) {
        text = text.toLowerCase();
        List<Item> items = itemStorage.searchItemByQuery(text);
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            itemDtos.add(itemDto);
        }

        return itemDtos;
    }


    @Override
    public ItemDto deleteItem(Long itemId, Long ownerId) {
        Item item = itemStorage.getItemById(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException("Ошибка! У пользователя отсутсвует данная вещь");
        }
        return itemMapper.toItemDto(itemStorage.deleteItem(itemId));
    }

    @Override
    public void deleteItemsByOwner(Long ownderId) {
        itemStorage.deleteItemsByOwner(ownderId);
    }


}
