package ru.practicum.shareit.item.itemBooking;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemBookingsDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemCommentsDto extends ItemBookingsDto {
    List<CommentDto> comments;

  }
