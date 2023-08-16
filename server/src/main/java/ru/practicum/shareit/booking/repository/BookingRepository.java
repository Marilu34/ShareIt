package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query(value = "SELECT b FROM Booking AS b " +
            "WHERE b.id = :bookingId " +
            "AND (b.item.owner.id = :userId OR b.booker.id = :userId)")
    Optional<Booking> findBookingByOwnerOrBooker(long bookingId, long userId);

    Stream<Booking> findAllByBookerId(long bookerId, Pageable page);

    Stream<Booking> findAllByBookerIdAndStatusIs(long bookerId, Status status, Pageable page);

    Stream<Booking> findAllByBookerIdAndEndIsBefore(long bookerId, LocalDateTime now, Pageable page);

    Stream<Booking> findAllByBookerIdAndStartIsAfter(long bookerId, LocalDateTime now, Pageable page);

    @Query(value = "SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH i.owner " +
            "JOIN FETCH b.booker " +
            "WHERE b.booker.id = :bookerId " +
            "AND :now BETWEEN b.start AND b.end")
    Stream<Booking> findAllCurrentBookerBookings(long bookerId, LocalDateTime now, Pageable page);

    Stream<Booking> findAllByItemOwnerIdAndStatusIs(long ownerId, Status status, Pageable page);

    Stream<Booking> findAllByItemOwnerIdAndStartIsAfter(long ownerId, LocalDateTime now, Pageable page);

    Stream<Booking> findAllByItemOwnerId(long ownerId, Pageable page);

    Stream<Booking> findAllByItemOwnerIdAndEndIsBefore(long ownerId, LocalDateTime now, Pageable page);

    @Query(value = "SELECT b FROM Booking AS b " +
            "JOIN FETCH b.item AS i " +
            "JOIN FETCH i.owner " +
            "JOIN FETCH b.booker " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND :now BETWEEN b.start AND b.end")
    Stream<Booking> findAllCurrentOwnerBookings(long ownerId, LocalDateTime now, Pageable page);

    // find next booking
    ShortBookingDto findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(long itemId,
                                                                              LocalDateTime now,
                                                                              Status status);

    // find last booking
    ShortBookingDto findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(long itemId,
                                                                             LocalDateTime now,
                                                                             Status status);

    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndEndBefore(long itemId, long bookerId,
                                                                    Status status, LocalDateTime now);

}
