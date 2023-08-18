package ru.practicum.shareit.request.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class ShortRequestsDto {
    @NotBlank @Length(max = 2024)
    private String description;
}
