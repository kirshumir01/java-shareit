package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.List;

public interface BookingService {

    BookingOutputDto create(long userId, BookingInputDto bookingInputDto);

    BookingOutputDto approvedByOwner(long userId, long bookingId, boolean approved);

    BookingOutputDto getBookingByIdAndUserId(long userId, long bookingId);

    List<BookingOutputDto> getAllByBooker(long userId, String state);

    List<BookingOutputDto> getAllByOwner(long userId, String state);

}
