package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingOutputDto create(long userId, BookingInputDto bookingInputDto) {
        validate(userId, bookingInputDto);

        User booker = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(String.format("Пользователь с id %d не найден.", userId)));

        Item item = itemRepository.findById(bookingInputDto.getItemId()).orElseThrow(
                () -> new BadRequestException(String.format("Вещь с идентификатором %d не найдена",
                        bookingInputDto.getItemId())));

        return BookingMapper.toBookingOutputDto(bookingRepository.save(
                Booking.builder()
                        .start(bookingInputDto.getStart())
                        .end(bookingInputDto.getEnd())
                        .item(item)
                        .booker(booker)
                        .status(Status.WAITING)
                        .build()
        ));
    }

    @Override
    @Transactional
    public BookingOutputDto approvedByOwner(long userId, long bookingId, boolean approved) {
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
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.toBookingOutputDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingOutputDto getBookingByIdAndUserId(long userId, long bookingId) {
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
        return BookingMapper.toBookingOutputDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutputDto> getAllByBooker(long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", userId));
        }

        return switch (state) {
            case "ALL" -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId)
                    .stream()
                    .map(BookingMapper::toBookingOutputDto)
                    .collect(Collectors.toList());
            case "CURRENT" -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                            userId, LocalDateTime.now(), LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::toBookingOutputDto)
                    .collect(Collectors.toList());
            case "PAST" -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::toBookingOutputDto)
                    .collect(Collectors.toList());
            case "FUTURE" ->
                    bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now())
                            .stream()
                            .map(BookingMapper::toBookingOutputDto)
                            .collect(Collectors.toList());
            case "REJECTED", "WAITING" ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.valueOf(state))
                            .stream()
                            .map(BookingMapper::toBookingOutputDto)
                            .collect(Collectors.toList());
            default -> throw new BadRequestException(String.format("Неизвестный статус состояния %s.", state));
        };
    }

    @Override
    public List<BookingOutputDto> getAllByOwner(long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", userId));
        }

        return switch (state) {
            case "ALL" -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId)
                    .stream()
                    .map(BookingMapper::toBookingOutputDto)
                    .collect(Collectors.toList());
            case "CURRENT" -> bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                            userId, LocalDateTime.now(), LocalDateTime.now())
                    .stream()
                    .map(BookingMapper::toBookingOutputDto)
                    .collect(Collectors.toList());
            case "PAST" ->
                    bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now())
                            .stream()
                            .map(BookingMapper::toBookingOutputDto)
                            .collect(Collectors.toList());
            case "FUTURE" ->
                    bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now())
                            .stream()
                            .map(BookingMapper::toBookingOutputDto)
                            .collect(Collectors.toList());
            case "REJECTED", "WAITING" ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, Status.valueOf(state))
                            .stream()
                            .map(BookingMapper::toBookingOutputDto)
                            .collect(Collectors.toList());
            default -> throw new BadRequestException(String.format("Неизвестный статус состояния %s.", state));
        };
    }

    private void validate(long userId, BookingInputDto bookingInputDto) {



        if (bookingInputDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(String.format("Время завершения аренды %s раньше текущего времени $s.",
                    bookingInputDto.getEnd(),
                    LocalDateTime.now()));
        }

        if (bookingInputDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(String.format("Время начала аренды %s раньше текущего времени $s.",
                    bookingInputDto.getStart(),
                    LocalDateTime.now()));
        }

        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart())) {
            throw new BadRequestException(String.format("Время завершения аренды %s раньше времени начала $s.",
                    bookingInputDto.getEnd(),
                    bookingInputDto.getStart()));
        }

        if (bookingInputDto.getEnd().equals(bookingInputDto.getStart())) {
            throw new BadRequestException(String.format("Время завершения аренды %s идентично времени начала $s.",
                    bookingInputDto.getEnd(),
                    bookingInputDto.getStart()));
        }

        Item item = itemRepository.findById(bookingInputDto.getItemId()).orElseThrow(
                () -> new NotFoundException(String.format("Вещь с идентификатором %d не найдена",
                        bookingInputDto.getItemId())));

        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Вещь забронирована.");
        }

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь недоступна для бронирования: статус - FALSE.");
        }

        if (bookingRepository.findById(item.getId())
                .stream()
                .anyMatch(booking -> (booking.getStart().isAfter(bookingInputDto.getStart())
                        && booking.getStart().isBefore(bookingInputDto.getEnd())
                        || booking.getEnd().isAfter(bookingInputDto.getStart())
                        && booking.getEnd().isBefore(bookingInputDto.getEnd())))

        ) {
            throw new ValidationException("Даты начала и завершения аренды пересекаются с уже существующими.");
        }
    }
}