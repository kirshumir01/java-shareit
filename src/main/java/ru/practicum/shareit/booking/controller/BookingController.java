package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.BadRequestException;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody @Valid BookingCreateDto bookingCreateDto) {
        log.info("Запрос на сохранение бронирования: POST /bookings");
        BookingDto booking = bookingService.create(userId, bookingCreateDto);
        log.info("Информация о бронировании сохранена: {}", booking.toString());
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable("bookingId") long bookingId,
                                      @RequestParam("approved") boolean approved) {
        log.info("Запрос на обновление бронирования: PATCH /bookings/{}", bookingId);
        BookingDto updatedBooking = bookingService.approvedByOwner(userId, bookingId, approved);
        log.info("Информация о бронировании обновлена: {}", updatedBooking.toString());
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable("bookingId") long bookingId) {
        log.info("Запрос на получение бронирования: GET /bookings/{}", bookingId);
        BookingDto booking = bookingService.getBookingByIdAndUserId(userId, bookingId);
        log.info("Информация о бронировании получена: {}", booking.toString());
        return booking;
    }

    @GetMapping
    public List<BookingDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "ALL", required = false) State state) {
        log.info("Запрос на получение всех бронирований пользователя с id = {} и статусом '{}': GET /bookings",
                userId, state);
        try {
            State.valueOf(String.valueOf(state));
        } catch (BadRequestException e) {
            throw new BadRequestException(String.format("Неизвестный статус состояния %s.", state));
        }
        List<BookingDto> bookings = bookingService.getAllByBooker(userId, state);
        log.info("Количество бронирований пользователя: {}", bookings.size());
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(defaultValue = "ALL", required = false) State state) {
        log.info("Запрос на получение всех бронирований арендодателя с id = {} и статусом '{}': GET /bookings",
                userId, state);
        try {
            State.valueOf(String.valueOf(state));
        } catch (BadRequestException e) {
            throw new BadRequestException(String.format("Неизвестный статус состояния %s.", state));
        }
        List<BookingDto> bookings = bookingService.getAllByOwner(userId, state);
        log.info("Количество бронирований арендодателя: {}", bookings.size());
        return bookings;
    }
}