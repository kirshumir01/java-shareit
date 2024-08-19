package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime time1, LocalDateTime time2);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime time);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime time);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status state);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long ownerId, LocalDateTime time1, LocalDateTime time2);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime time);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime time);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, Status state);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findAllByBookerIdAndItemIdAndStatusAndEndBefore(long bookerId, long itemId, Status status, LocalDateTime time);

    @EntityGraph(attributePaths = {"item"})
    Optional<Booking> findFirstByItemIdAndStartLessThanEqualAndStatus(
            long itemId, LocalDateTime now, Status status, Sort end);

    @EntityGraph(attributePaths = {"item"})
    List<Booking> findByItemInAndStartLessThanEqualAndStatus
            (List<Item> items, LocalDateTime now, Status approved, Sort end);

    @EntityGraph(attributePaths = {"item"})
    Optional<Booking> findFirstByItemIdAndStartAfterAndStatus
            (long itemId, LocalDateTime now, Status status, Sort end);

    @EntityGraph(attributePaths = {"item"})
    List<Booking> findByItemInAndStartAfterAndStatus
            (List<Item> items, LocalDateTime now, Status approved, Sort end);
}