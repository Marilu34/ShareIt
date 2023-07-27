package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
public class UserDto {

    long id; //уникальный идентификатор пользователя
    String name; //имя или логин пользователя
    String email; //адрес электронной почты
}
