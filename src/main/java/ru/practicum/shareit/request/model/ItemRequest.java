package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


@Data
public class ItemRequest {

    long id; //уникальный идентификатор запроса;
    String description; //текст запроса, содержащий описание требуемой вещи;
    User requestor; //пользователь, создавший запрос;
    LocalDateTime created;  //дата и время создания запроса.
}
