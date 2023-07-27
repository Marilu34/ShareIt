package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDTO = new CommentDto();
        commentDTO.setId(comment.getId());
        commentDTO.setText(comment.getText());
        commentDTO.setAuthorName(comment.getAuthor().getName());
        commentDTO.setCreated(comment.getCreated());
        return commentDTO;
    }

    public static Comment fromCommentDto(CommentDto commentDTO) {
        Comment comment = new Comment();
        comment.setId(commentDTO.getId());
        comment.setText(commentDTO.getText());
        comment.setCreated(commentDTO.getCreated());
        return comment;
    }
}
