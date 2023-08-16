package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.id = :bookingId " +
            "AND (b.item.owner.id = :userId OR b.booker.id = :userId)")
    Optional<Booking> findBookingByOwnerOrBooker(long bookingId, long userId);
    //ищет Booking, используя идентификатор (bookingId) и идентификатор владельца (userId) или бронирующего (userId).

    Stream<Booking> findAllByBookerId(long bookerId, Pageable sort);
    //находит все Bookings, связанные с определенным booker'ом по его идентификатору (bookerId).

    Stream<Booking> findAllByBookerIdAndStatusIs(long bookerId, Status status, Pageable page);
    //находит все Bookings, принадлежащие конкретному booker'у (bookerId) и имеющие определенный статус (status).

    Stream<Booking> findAllByBookerIdAndEndIsBefore(long bookerId, LocalDateTime now, Pageable page);
    //находит все Bookings, у которых время окончания (end) раньше заданного времени (now), принадлежащие определенному booker'у (bookerId).

    Stream<Booking> findAllByBookerIdAndStartIsAfter(long bookerId, LocalDateTime now, Pageable page);
    //находит все Bookings, у которых время начала (start) позже заданного времени (now), принадлежащие определенному booker'у (bookerId).

    @Query(value = "SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH i.owner " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND :now BETWEEN b.start AND b.end")
    Stream<Booking> findAllCurrentBookerBookings(long bookerId, LocalDateTime now, Pageable page);
    //находит все текущие Bookings (те, у которых текущее время находится между start и end),
    // принадлежащие определенному booker'у (bookerId).

    Stream<Booking> findAllByItemOwnerIdAndStatusIs(long ownerId, Status status, Pageable page);
    //находит все Bookings, принадлежащие владельцу (ownerId) и имеющие определенный статус (status).

    Stream<Booking> findAllByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime now, Pageable page);
    //находит все Bookings, у которых время начала (start) позже заданного времени (now),
    // принадлежащие определенному владельцу (ownerId).

    Stream<Booking> findAllByItemOwnerId(long ownerId, Pageable page);
    //находит все Bookings, принадлежащие определенному владельцу (ownerId).

    Stream<Booking> findAllByItemOwnerIdAndEndIsBefore(long ownerId, LocalDateTime now, Pageable page);
    //находит все Bookings, у которых время окончания (end) раньше заданного времени (now),
    // принадлежащие определенному владельцу (ownerId).

    @Query(value = "SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH i.owner " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND :now BETWEEN b.start AND b.end")
    Stream<Booking> findAllCurrentOwnerBookings(long ownerId, LocalDateTime now, Pageable page);
    //аходит все текущие Bookings (те, у которых текущее время находится между start и end), принадлежащие определенному владельцу (ownerId).


    ShortBookingDto findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(long itemId,
                                                                              LocalDateTime now,
                                                                              Status status);
    //находит первое бронирование (BookingIdAndBookerIdOnly) по идентификатору предмета (itemId),
    // с временем начала позже заданного времени (now), и статусом не равным определенному статусу (status).


    ShortBookingDto findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(long itemId,
                                                                             LocalDateTime now,
                                                                             Status status);
    //находит последнее бронирование (BookingIdAndBookerIdOnly) по идентификатору предмета (itemId),
    // с временем начала раньше заданного времени (now),и определенным статусом (status),
    // сортированное по времени начала в убывающем порядкe

    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(long itemId, long bookerId,
                                                                    Status status, LocalDateTime now);
    //находит все Bookings по идентификатору предмета (itemId), идентификатору booker'а (bookerId),
    // определенному статусу (status) и которые заканчиваются раньше заданного времени (now).

}
