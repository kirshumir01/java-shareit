package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validationgroups.Create;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public BookingOutputDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @Validated({Create.class}) @RequestBody BookingInputDto bookingInputDto) {
        log.info("Запрос на сохранение информации о новом бронировании {}", bookingInputDto.toString());
        BookingOutputDto booking = bookingService.create(userId, bookingInputDto);
        log.info("Информация о новом бронировании вещи с id = {} сохранена", bookingInputDto.getItemId());
        log.info("Информация об арендаторе: {}", booking.getBooker().toString());
        log.info("Время начала аренды: {}", booking.getStart());
        log.info("Время завершения аренды: {}", booking.getEnd());
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approvedByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable("bookingId") long bookingId,
                                            @RequestParam("approved") boolean approved) {
        log.info("Запрос на обновление информации о бронировании c id = {}", bookingId);
        BookingOutputDto updatedBooking = bookingService.approvedByOwner(userId, bookingId, approved);
        log.info("Обновленная информация о бронировании с id = {}", bookingId);
        log.info("Информация об арендаторе: {}", updatedBooking.getBooker().toString());
        log.info("Время начала аренды: {}", updatedBooking.getStart());
        log.info("Время завершения аренды: {}", updatedBooking.getEnd());
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                                @PathVariable("bookingId") long bookingId) {
        log.info("Запрос на получение информации о бронировании с id = {} пользователем с id = {}", bookingId, userId);
        BookingOutputDto booking = bookingService.getBookingByIdAndUserId(userId, bookingId);
        log.info("Информация о бронировании с id = {}", bookingId);
        log.info("Информация об арендаторе: {}", booking.getBooker().toString());
        log.info("Время начала аренды: {}", booking.getStart());
        log.info("Время завершения аренды: {}", booking.getEnd());
        return booking;
    }

    @GetMapping
    public List<BookingOutputDto> getAllByClient(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Запрос на получение информации обо всех бронированиях пользователем с id = {} и статусом '{}'",
                userId, state);
        List<BookingOutputDto> bookings = bookingService.getAllByBooker(userId, state);
        log.info("Информация о бронированиях пользователем с id = {}:", userId);
        log.info("Количество бронирований со статусом '{}': {}", bookings.size(), state);
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Запрос на получение информации обо всех бронированиях арендодателя с id = {} и статусом '{}'",
                userId, state);
        List<BookingOutputDto> bookings = bookingService.getAllByOwner(userId, state);
        log.info("Информация о бронированиях арендодателя с id = {}:", userId);
        log.info("Количество бронирований со статусом '{}': {}", bookings.size(), state);
        return bookings;
    }
}