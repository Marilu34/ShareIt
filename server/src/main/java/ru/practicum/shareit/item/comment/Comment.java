package ru.practicum.shareit.item.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private long id;


    @Column(name = "comment_text", nullable = false)
    private String text;

    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;


    @JoinColumn(name = "author_id", referencedColumnName = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;


    @Column(nullable = false)
    private LocalDateTime created;
}