package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class Item {

    private Long id;
    @NotBlank private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
    private Long requestId;
}