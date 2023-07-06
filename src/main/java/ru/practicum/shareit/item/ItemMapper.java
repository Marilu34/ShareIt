package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
@Component
public class  ItemMapper {


    public ItemDto mapToItemDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemDto.ItemDtoBuilder itemDto = ItemDto.builder();

        itemDto.id(item.getId());
        itemDto.name(item.getName());
        itemDto.description(item.getDescription());
        itemDto.available(item.getAvailable());
        itemDto.owner(item.getOwner());
        itemDto.request(item.getRequest());

        return itemDto.build();
    }


    public Item mapToItem(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }

        Item.ItemBuilder item = Item.builder();

        item.id(itemDto.getId());
        item.name(itemDto.getName());
        item.description(itemDto.getDescription());
        item.available(itemDto.getAvailable());
        item.owner(itemDto.getOwner());
        item.request(itemDto.getRequest());

        return item.build();
    }
}