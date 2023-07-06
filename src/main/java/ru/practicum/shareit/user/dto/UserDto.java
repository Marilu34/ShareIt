package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "имя не должно быть пустым")
    private String name;
    @Email
    @NotNull
    private String email;
    private List<UserDto> users;
}