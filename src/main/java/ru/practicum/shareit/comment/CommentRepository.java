package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT new ru.practicum.shareit.comment.dto.CommentDto(c.id, c.text, c.author.name, c.created) " +
            "FROM Comment AS c " +
            "WHERE c.item.id = :itemId")
    List<CommentDto> getAllCommentsByItems(long itemId);
}

