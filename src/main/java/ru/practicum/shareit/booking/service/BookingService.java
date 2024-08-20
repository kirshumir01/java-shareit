package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    BookingDto create(long userId, BookingCreateDto bookingCreateDto);

    BookingDto approvedByOwner(long userId, long bookingId, boolean approved);

    BookingDto getBookingByIdAndUserId(long userId, long bookingId);

    List<BookingDto> getAllByBooker(long userId, State state);

    List<BookingDto> getAllByOwner(long userId, State state);

}