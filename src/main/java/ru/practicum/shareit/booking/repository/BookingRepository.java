package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status state);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long ownerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime time);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime time);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, Status state);

    List<Booking> findAllByBookerIdAndItemIdAndStatusAndEndBefore(long userId, long itemId, Status status, LocalDateTime time);

    @Query(value = """
            SELECT * FROM bookings
            WHERE item_id = :itemId AND end_time < :now
            ORDER BY end_time ASC LIMIT 1
            """,
            nativeQuery = true)
    Optional<Booking> getLastBooking(long itemId, LocalDateTime now);

    @Query(value = """
            SELECT * FROM bookings
            WHERE item_id = :itemId AND start_time > :now AND status != 'REJECTED'
            ORDER BY start_time ASC LIMIT 1
            """,
            nativeQuery = true)
    Optional<Booking> getNextBooking(long itemId, LocalDateTime now);
}