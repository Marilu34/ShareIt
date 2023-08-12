package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testEquals_shouldNotBeEqual_whenNew() {
        assertNotEquals(new Comment(), new Comment());
    }

    @Test
    void testHashCode_shouldHaveSameHash_whenObjectsAreEqual() {
        Comment comment1 = new Comment();
        comment1.setId(5);
        Comment comment2 = new Comment();
        comment2.setId(5);

        assertFalse(comment1.equals(comment2));

    }
}