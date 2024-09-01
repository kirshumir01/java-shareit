package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InternalServerError;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto create(long userId, BookingCreateDto bookingCreateDto) {
        validate(userId, bookingCreateDto);

        User booker = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id = %d not found", userId)));

        Item item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(
                () -> new NotFoundException(String.format("Item with id = %d not found",
                        bookingCreateDto.getItemId())));
        Booking bookingForCreate = BookingMapper.toBooking(booker, item, bookingCreateDto);
        bookingForCreate.setStatus(BookingStatus.WAITING);
        Booking createdBooking = bookingRepository.save(bookingForCreate);
        return BookingMapper.toBookingDto(createdBooking);
    }

    @Override
    @Transactional
    public BookingDto approvedByOwner(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Booking with id = %d not found", bookingId)));

        if (!userRepository.existsById(userId)) {
            throw new BadRequestException(String.format("Incorrect user id: %d.", userId));
        }

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException(String.format("Item '%s' of user with id = %d not found",
                    booking.getItem().getName(), userId));
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequestException(String.format("Booking status already set up: %s.",
                    booking.getStatus()));
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException(String.format("Item '%s' already booked", booking.getItem().getName()));
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingByIdAndUserId(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Boooking with id = %d not found", bookingId)));

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }

        if (!Objects.equals(booking.getBooker().getId(), userId) &&
                !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException(String.format("Item or booking by user with id = %d not found", userId));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByBooker(long userId, BookingState state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, LocalDateTime.now(), LocalDateTime.now());
            case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE ->
                    bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case REJECTED, WAITING ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                            userId, BookingStatus.valueOf(String.valueOf(state)));
        };
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    @Override
    public List<BookingDto> getAllByOwner(long userId, BookingState state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                    userId, LocalDateTime.now(), LocalDateTime.now());
            case PAST ->
                    bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
            case FUTURE ->
                    bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
            case REJECTED, WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                            userId, BookingStatus.valueOf(String.valueOf(state)));
        };
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    private void validate(long userId, BookingCreateDto bookingCreateDto) {
        if (bookingCreateDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(String.format("Completion rent time of item '%s' is earlier than current time %s",
                    bookingCreateDto.getEnd(),
                    LocalDateTime.now()));
        }

        if (bookingCreateDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(String.format("Start rent time of item %s is earlier than current time $s.",
                    bookingCreateDto.getStart(),
                    LocalDateTime.now()));
        }

        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart())) {
            throw new BadRequestException(String.format("Completion rent time of item %s is earlier than start rent time $s.",
                    bookingCreateDto.getEnd(),
                    bookingCreateDto.getStart()));
        }

        if (bookingCreateDto.getEnd().equals(bookingCreateDto.getStart())) {
            throw new BadRequestException(String.format("Completion rent time of item '%s' is equals to start rent time %s",
                    bookingCreateDto.getEnd(),
                    bookingCreateDto.getStart()));
        }

        Item item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(
                () -> new NotFoundException(String.format("Item with id = %d not found",
                        bookingCreateDto.getItemId())));

        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Item is booked");
        }

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new InternalServerError("Item is unavailable for booking: status of booking - FALSE");
        }

        if (bookingRepository.findById(item.getId())
                .stream()
                .anyMatch(booking -> (booking.getStart().isAfter(bookingCreateDto.getStart())
                        && booking.getStart().isBefore(bookingCreateDto.getEnd())
                        || booking.getEnd().isAfter(bookingCreateDto.getStart())
                        && booking.getEnd().isBefore(bookingCreateDto.getEnd())))

        ) {
            throw new BadRequestException("Start and completion rent dates are crossed with current dates");
        }
    }
}