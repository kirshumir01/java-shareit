package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") @Positive long userId,
                                                @RequestBody @Valid BookingCreateDto bookingCreateDto) {
        log.info("Gateway: received request from userId = {} for booking with parameters {}", userId, bookingCreateDto);
        return bookingClient.create(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> approvedByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable("bookingId") long bookingId,
                                                  @RequestParam(name = "approved") boolean approved) {
        log.info("Gateway: received request from userId = {} to set approve \"{}\" for bookingId = {}", userId, approved, bookingId);
        return bookingClient.approvedByOwner(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable("bookingId") long bookingId) {
        log.info("Gateway: received request from userId = {} to get booking by id = {}", userId, bookingId);
        return bookingClient.get(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "state",
                                                         required = false,
                                                         defaultValue = "ALL") BookingState state) {
        log.info("Gateway: received request to get all bookings by userId = {} and state = {}", userId, state);
        return bookingClient.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(name = "state",
                                                        required = false,
                                                        defaultValue = "ALL") BookingState state) {
        log.info("Gateway: received request to get all bookings by ownerId = {} and state = {}", userId, state);
        return bookingClient.getAllByOwner(userId, state);
    }
}