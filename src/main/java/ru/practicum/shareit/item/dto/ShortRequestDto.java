package ru.practicum.shareit.item.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class ShortRequestDto {
    long requesterId;

    @NotBlank
    @Length(max = 2024)
    private String description;
}
