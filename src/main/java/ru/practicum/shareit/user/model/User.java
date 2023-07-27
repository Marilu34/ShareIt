package ru.practicum.shareit.user.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users",
        uniqueConstraints = {@UniqueConstraint(columnNames = "email")}
)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Имя не может быть пустым!")
    private String name; //имя или логин пользователя

    @Column(name = "email")
    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @!")
    private String email; //адрес электронной почты

}
