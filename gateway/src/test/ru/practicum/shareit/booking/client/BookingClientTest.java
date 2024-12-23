package ru.practicum.shareit.booking.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {
    @Spy
    private final BookingClient bookingClient =
            new BookingClient("http://localhost:9090", new RestTemplateBuilder());

    @Test
    void createBookingTest() {
        long userId = 1L;
        long itemId = 1L;
        String path = "";

        BookingCreateDto bookingCreateDto = new BookingCreateDto(itemId, null, null);

        assertThrows(Throwable.class, () -> bookingClient.create(userId, bookingCreateDto));
        assertThrows(Throwable.class, () -> bookingClient.post(path, userId, null, bookingCreateDto));
    }

    @Test
    void approvedByOwnerTest() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        String path = String.format("/%d", bookingId);

        Map<String, String> query = Map.of("approved", Boolean.TRUE.toString());

        assertThrows(Throwable.class, () -> bookingClient.approvedByOwner(userId, bookingId, approved));
        assertThrows(Throwable.class, () -> bookingClient.patch(path, userId, query, null));
    }

    @Test
    void getBookingStatusTest() {
        long userId = 1L;
        long bookingId = 1L;
        String path = String.format("/%d", bookingId);

        assertThrows(Throwable.class, () -> bookingClient.get(userId, bookingId));
        assertThrows(Throwable.class, () -> bookingClient.get(path, userId, null));
    }

    @Test
    void getAllBookingsByBookerTest() {
        long userId = 1L;
        BookingState state = BookingState.ALL;
        String path = "";

        Map<String, String> query = Map.of("state", state.name());

        assertThrows(Throwable.class, () -> bookingClient.getAllByBooker(userId, state));
        assertThrows(Throwable.class, () -> bookingClient.get(path, userId, query));
    }

    @Test
    void getAllBookingsBuOwnerTest() {
        String path = "/owner";
        long userId = 1L;
        BookingState state = BookingState.ALL;

        Map<String, String> query = Map.of("state", state.name());

        assertThrows(Throwable.class, () -> bookingClient.getAllByOwner(userId, state));
        assertThrows(Throwable.class, () -> bookingClient.get(path, userId, query));
    }
}