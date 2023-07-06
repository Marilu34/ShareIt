package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private ItemRequest request;
  //   @JsonValue
    private List<ItemDto> items;
}
