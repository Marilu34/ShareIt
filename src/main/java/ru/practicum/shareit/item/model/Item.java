package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "items")
public class Item implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; //уникальный идентификатор вещи

    @Column(name = "name", nullable = false)
    @NotNull
    String name; // краткое название

    @Column(name = "description", nullable = false)
    @NotNull
    String description; //развёрнутое описание

    @Column(name = "available", nullable = false)
    @NotNull
    Boolean available; //статус о том, доступна или нет вещь для аренды

    @JoinColumn(name = "owner")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    User owner;  //владелец вещи

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "item_id")
    List<Comment> comments;

    @Column(name = "request")
    long request; //ссылка на запрос
}
