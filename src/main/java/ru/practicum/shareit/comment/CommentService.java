package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.ValidationException;

public interface CommentService {

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDTO) throws ValidationException;
}
