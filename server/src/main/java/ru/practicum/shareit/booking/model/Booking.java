package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id", nullable = false)
    private long id;


    @JoinColumn(name = "item_id", referencedColumnName = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @JoinColumn(name = "booker_id", referencedColumnName = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User booker;


    @Column(name = "start_date", nullable = false)
    private LocalDateTime rentStartDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime rentEndDate;


    @Enumerated
    @Column(nullable = false)
    private Status status;
}