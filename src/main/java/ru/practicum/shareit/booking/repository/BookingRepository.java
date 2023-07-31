package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Status;
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

    Stream<Booking> findAllByBookerId(long bookerId, Sort sort);

    Stream<Booking> findAllByBookerIdAndStatusIs(long bookerId, Status status, Sort sort);

    Stream<Booking> findAllByBookerIdAndEndIsBefore(long bookerId, LocalDateTime now, Sort sort);

    Stream<Booking> findAllByBookerIdAndStartIsAfter(long bookerId, LocalDateTime now, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH i.owner " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND :now BETWEEN b.start AND b.end")
    Stream<Booking> findAllCurrentBookerBookings(long bookerId, LocalDateTime now, Sort sort);

    Stream<Booking> findAllByItemOwnerIdAndStatusIs(long ownerId, Status status, Sort sort);

    Stream<Booking> findAllByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime now, Sort sort);

    Stream<Booking> findAllByItemOwnerId(long ownerId, Sort sort);

    Stream<Booking> findAllByItemOwnerIdAndEndIsBefore(long ownerId, LocalDateTime now, Sort sort);

    @Query(value = "SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH i.owner " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND :now BETWEEN b.start AND b.end")
    Stream<Booking> findAllCurrentOwnerBookings(long ownerId, LocalDateTime now, Sort sort);

    // find next booking
    BookingIdAndBookerIdOnly findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(long itemId,
                                                                                       LocalDateTime now,
                                                                                       Status status);

    // find last booking
    BookingIdAndBookerIdOnly findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(long itemId,
                                                                                      LocalDateTime now,
                                                                                      Status status);

    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(long itemId, long bookerId,
                                                                    Status status, LocalDateTime now);

}
