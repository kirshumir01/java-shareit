package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String header = "X-Sharer-User-Id";

    @Test
    void createBookingTest() throws Exception {
        long userId = 1L;
        BookingCreateDto bookingCreateDto = setBookingCreateDto();

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.CREATED);

        when(bookingClient.create(userId, bookingCreateDto)).thenReturn(response);

        mvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .header(header, userId))
                .andExpect(status().isCreated());

        verify(bookingClient, times(1)).create(userId, bookingCreateDto);
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    void approwedByOwnerTest() throws Exception {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

        when(bookingClient.approvedByOwner(anyLong(), anyByte(), anyBoolean())).thenReturn(response);

        mvc.perform(patch("/bookings/" + bookingId + "?approved=" + approved)
                        .header(header, userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).approvedByOwner(anyLong(), anyLong(), anyBoolean());
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    void getBookingStatusTest() throws Exception {
        long userId = 12213;
        long bookingId = 1312;

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

        when(bookingClient.get(anyLong(), anyLong())).thenReturn(response);

        mvc.perform(get("/bookings/" + bookingId)
                        .header(header, userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).get(anyLong(), anyLong());
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    void getAllBookingsByBookerTest() throws Exception {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

        when(bookingClient.getAllByBooker(userId, state)).thenReturn(response);

        mvc.perform(get("/bookings?state=" + state)
                        .header(header, userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getAllByBooker(anyLong(), any(BookingState.class));
        verifyNoMoreInteractions(bookingClient);
    }

    @Test
    void getAllBookingsByOwnerTest() throws Exception {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

        when(bookingClient.getAllByOwner(userId, state)).thenReturn(response);

        mvc.perform(get("/bookings/owner?state=" + state)
                        .header(header, userId))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).getAllByOwner(anyLong(), any(BookingState.class));
        verifyNoMoreInteractions(bookingClient);
    }

    private BookingCreateDto setBookingCreateDto() {
        return BookingCreateDto.builder()
                .itemId(1L)
                .start(null)
                .end(null)
                .build();
    }
}