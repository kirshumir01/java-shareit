package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Server: received request from userId = {} for booking with parameters {}", userId, bookingCreateDto);
        return bookingService.create(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approvedByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable("bookingId") long bookingId,
                                      @RequestParam(name = "approved", required = false) boolean approved) {
        log.info("Server: received request from userId = {} to set approve '{}' for bookingId = {}",
                userId, approved, bookingId);
        return bookingService.approvedByOwner(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable("bookingId") long bookingId) {
        log.info("Server: received request from userId = {} to get booking by id = {}", userId, bookingId);
        return bookingService.getBookingByIdAndUserId(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "ALL", required = false) BookingState state) {
        log.info("Server: received request to get all bookings by userId = {} and state = {}", userId, state);
        BookingState.valueOf(String.valueOf(state));
        return bookingService.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(defaultValue = "ALL", required = false) BookingState state) {
        log.info("Server: received request to get all bookings by ownerId = {} and state = {}", userId, state);
        BookingState.valueOf(String.valueOf(state));
        return bookingService.getAllByOwner(userId, state);
    }
}