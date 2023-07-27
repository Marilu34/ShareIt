package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@Entity
@Table(name = "bookings")
public class Booking implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;  //уникальный идентификатор бронирования
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "start_date")
    LocalDateTime start; //дата и время начала бронирования
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "end_date")
    LocalDateTime end; //дата и время конца бронирования

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.EAGER)
    Item item; //вещь, которую пользователь бронирует

    @JoinColumn(name = "booker_id")
    @ManyToOne(fetch = FetchType.EAGER)
    User booker; //пользователь, который осуществляет бронирование

    @Enumerated(EnumType.STRING)
    @Column
    Status status; //статус бронирования


}
