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
    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId, LocalDateTime time1, LocalDateTime time2);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime time);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime time);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status state);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(long ownerId);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(long ownerId, LocalDateTime time1, LocalDateTime time2);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime time);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(long ownerId, LocalDateTime time);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, Status state);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllByBookerIdAndItemIdAndStatusAndEndBefore(
            long bookerId, long itemId, Status status, LocalDateTime time);

    @EntityGraph(value = "booking-entity-graph")
    Optional<Booking> findFirstByItemIdAndStartLessThanEqualAndStatus(
            long itemId, LocalDateTime now, Status status, Sort end);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findByItemInAndStartLessThanEqualAndStatus(
            List<Item> items, LocalDateTime now, Status approved, Sort end);

    @EntityGraph(value = "booking-entity-graph")
    Optional<Booking> findFirstByItemIdAndStartAfterAndStatus(
            long itemId, LocalDateTime now, Status status, Sort end);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findByItemInAndStartAfterAndStatus(
            List<Item> items, LocalDateTime now, Status approved, Sort end);
}