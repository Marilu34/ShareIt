package ru.practicum.shareit.comment.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; //уникальный идентификатор комментария

    @Column(name = "text", nullable = false)
    String text; // содержимое комментария

    @Column(name = "item_id")
    long itemId; //вещь, к которой относится комментарий

    @JoinColumn(name = "author_id")
    @ManyToOne(fetch = FetchType.EAGER)
    User author; //автор комментария;

    @Column(name = "created", nullable = false)
    LocalDateTime created; //дата создания комментария
}
