package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestDto {
    long id;
    String description;
    String created;

    public RequestDto(RequestDto obj) {
        this.id = obj.id;
        this.description = obj.description;
        this.created = obj.created;
    }
}
