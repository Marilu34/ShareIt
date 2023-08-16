package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Data
@NoArgsConstructor
public class RequestList extends RequestDto {

    private List<ItemDto> items;

    public RequestList(RequestDto requestDto, List<ItemDto> items) {
        super(requestDto);
        this.items = items != null ? items : List.of();
    }
}
