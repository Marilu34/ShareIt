package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                          LocalDateTime timeStart,
                                                                          LocalDateTime timeEnd);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findByItem_OwnerIdOrderByStartDesc(long userId);

    List<Booking> findByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                              LocalDateTime timeStart,
                                                                              LocalDateTime timeEnd);

    List<Booking> findByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByItem_OwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByItem_OwnerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime endDate);

    List<Booking> findByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime startDate);

    @Query(value = "select * " +
            "from public.bookings " +
            "where item_id = ?1 " +
            "and booker_id = ?2 " +
            "and status = ?3 " +
            "and start_date < ?4 " +
            "order by start_date desc", nativeQuery = true)
    List<Booking> findBookingByItemAndUser(long itemId, long userId, String status, LocalDateTime date);
}
