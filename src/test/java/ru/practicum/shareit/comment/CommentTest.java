package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.model.Comment;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

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

        assertTrue(comment1.equals(comment2));
        assertEquals(comment1.hashCode(), comment2.hashCode());
    }
}