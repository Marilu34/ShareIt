package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

class CommentMapperTest {

    @Test
    public void toCommentDto_ReturnsCorrectCommentDto() {

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");

        User author = new User();
        author.setName("John");

        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());


        CommentDto commentDto = CommentMapper.toCommentDto(comment);


        Assertions.assertEquals(comment.getId(), commentDto.getId());
        Assertions.assertEquals(comment.getText(), commentDto.getText());
        Assertions.assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        Assertions.assertEquals(comment.getCreated(), commentDto.getCreated());
    }

    @Test
    public void toComment_ReturnsCorrectComment() {

        String text = "Test comment";

        User author = new User();
        author.setName("John");

        Item item = new Item();


        Comment comment = CommentMapper.toComment(text, author, item);


        Assertions.assertEquals(text, comment.getText());
        Assertions.assertEquals(author, comment.getAuthor());
        Assertions.assertEquals(item, comment.getItem());
        Assertions.assertNotNull(comment.getCreated());
    }
}