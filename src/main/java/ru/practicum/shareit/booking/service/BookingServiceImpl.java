package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

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
                () -> new NotFoundException(String.format("Пользователь с id %d не найден.", userId)));

        Item item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(
                () -> new NotFoundException(String.format("Вещь с идентификатором %d не найдена",
                        bookingCreateDto.getItemId())));
        Booking bookingForCreate = BookingMapper.toBooking(booker, item, bookingCreateDto);
        bookingForCreate.setStatus(Status.WAITING);
        Booking createdBooking = bookingRepository.save(bookingForCreate);
        return bookingMapper.toBookingDto(createdBooking);
    }

    @Override
    @Transactional
    public BookingDto approvedByOwner(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Бронирование с идентификатором %d не найдено.", bookingId)));

        if (!userRepository.existsById(userId)) {
            throw new ValidationException(String.format("Некорректный id пользователя: %d.", userId));
        }

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException(String.format("У пользователя с идентификатором %d не найдена вещь %s.",
                    userId, booking.getItem().getName()));
        }

        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new BadRequestException(String.format("Статус бронирования уже установлен: %s.",
                    booking.getStatus()));
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new BadRequestException(String.format("Вещь %s уже забронирована", booking.getItem().getName()));
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingByIdAndUserId(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException(String.format("Бронирование с идентификатором %d не найдено.", bookingId)));

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", userId));
        }

        if (!Objects.equals(booking.getBooker().getId(), userId) &&
                !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException(String.format("У пользователя с идентификатором %d не найдена вещь " +
                    "или бронирование.", userId));
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByBooker(long userId, State state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", userId));
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
                            userId, Status.valueOf(String.valueOf(state)));
        };
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    @Override
    public List<BookingDto> getAllByOwner(long userId, State state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", userId));
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
                            userId, Status.valueOf(String.valueOf(state)));
        };
        return bookings.stream().map(BookingMapper::toBookingDto).toList();
    }

    private void validate(long userId, BookingCreateDto bookingCreateDto) {
        if (bookingCreateDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(String.format("Время завершения аренды %s раньше текущего времени $s.",
                    bookingCreateDto.getEnd(),
                    LocalDateTime.now()));
        }

        if (bookingCreateDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(String.format("Время начала аренды %s раньше текущего времени $s.",
                    bookingCreateDto.getStart(),
                    LocalDateTime.now()));
        }

        if (bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart())) {
            throw new BadRequestException(String.format("Время завершения аренды %s раньше времени начала $s.",
                    bookingCreateDto.getEnd(),
                    bookingCreateDto.getStart()));
        }

        if (bookingCreateDto.getEnd().equals(bookingCreateDto.getStart())) {
            throw new BadRequestException(String.format("Время завершения аренды %s идентично времени начала $s.",
                    bookingCreateDto.getEnd(),
                    bookingCreateDto.getStart()));
        }

        Item item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(
                () -> new NotFoundException(String.format("Вещь с идентификатором %d не найдена",
                        bookingCreateDto.getItemId())));

        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Вещь забронирована.");
        }

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь недоступна для бронирования: статус - FALSE.");
        }

        if (bookingRepository.findById(item.getId())
                .stream()
                .anyMatch(booking -> (booking.getStart().isAfter(bookingCreateDto.getStart())
                        && booking.getStart().isBefore(bookingCreateDto.getEnd())
                        || booking.getEnd().isAfter(bookingCreateDto.getStart())
                        && booking.getEnd().isBefore(bookingCreateDto.getEnd())))

        ) {
            throw new ValidationException("Даты начала и завершения аренды пересекаются с уже существующими.");
        }
    }
}