package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService mockBookingService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String header = "X-Sharer-User-Id";

    @Test
    void createBookingTest() throws Exception {
        long ownerId = 1L;
        long userId = 2L;

        UserDto userDto = setUserDto(userId);
        ItemDto itemDto = setItemDto(ownerId);
        BookingDto bookingDto = setBookingDto(userDto, itemDto);

        BookingCreateDto bookingCreateDto = new BookingCreateDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(mockBookingService.create(anyLong(), any(BookingCreateDto.class))).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingCreateDto))
                        .header(header, userId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(is(itemDto.getId()), Long.class));

        verify(mockBookingService, times(1)).create(userId, bookingCreateDto);
        verifyNoMoreInteractions(mockBookingService);

    }

    @Test
    void answerBookingRequestTest() throws Exception {
        long ownerId = 1L;
        long userId = 2L;
        long bookingId = 1L;
        boolean approved = true;

        UserDto userDto = setUserDto(userId);
        ItemDto itemDto = setItemDto(ownerId);
        BookingDto bookingDto = setBookingDto(userDto, itemDto);


        when(mockBookingService.approvedByOwner(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/" + bookingId + "?approved=" + approved)
                        .contentType("application/json")
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(is(itemDto.getId()), Long.class));

        verify(mockBookingService, times(1)).approvedByOwner(anyLong(), anyLong(), anyBoolean());
        verifyNoMoreInteractions(mockBookingService);
    }

    @Test
    void getBookingStatusTest() throws Exception {
        long ownerId = 1L;
        long userId = 2L;
        long bookingId = 1L;

        UserDto userDto = setUserDto(userId);
        ItemDto itemDto = setItemDto(ownerId);
        BookingDto bookingDto = setBookingDto(userDto, itemDto);

        when(mockBookingService.getBookingByIdAndUserId(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/" + bookingId)
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(is(itemDto.getId()), Long.class));

        verify(mockBookingService, times(1)).getBookingByIdAndUserId(anyLong(), anyLong());
        verifyNoMoreInteractions(mockBookingService);
    }

    @Test
    void getAllBookingsByUserTest() throws Exception {
        long ownerId = 1L;
        long userId = 2L;
        BookingState state = BookingState.ALL;

        UserDto userDto = setUserDto(userId);
        ItemDto itemDto = setItemDto(ownerId);
        BookingDto bookingDto = setBookingDto(userDto, itemDto);
        List<BookingDto> bookingDtoList = List.of(bookingDto);

        when(mockBookingService.getAllByBooker(anyLong(), any(BookingState.class))).thenReturn(bookingDtoList);

        mvc.perform(get("/bookings?state=" + state)
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(is(1L), Long.class));

        verify(mockBookingService, times(1)).getAllByBooker(anyLong(), any(BookingState.class));
        verifyNoMoreInteractions(mockBookingService);
    }

    @Test
    void getAllBookingsForUserItemsTest() throws Exception {
        long ownerId = 1L;
        long userId = 2L;
        BookingState state = BookingState.ALL;

        UserDto userDto = setUserDto(userId);
        ItemDto itemDto = setItemDto(ownerId);
        BookingDto bookingDto = setBookingDto(userDto, itemDto);
        List<BookingDto> bookingDtoList = List.of(bookingDto);

        when(mockBookingService.getAllByOwner(userId, state)).thenReturn(bookingDtoList);

        mvc.perform(get("/bookings/owner?state=" + state)
                        .header(header, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(is(1L), Long.class));

        verify(mockBookingService, times(1)).getAllByOwner(anyLong(), any(BookingState.class));
        verifyNoMoreInteractions(mockBookingService);
    }

    private ItemDto setItemDto(long ownerId) {
        return ItemDto.builder()
                .id(1L)
                .name("TestItem")
                .description("Test description")
                .requestId(1L)
                .available(true)
                .ownerId(ownerId)
                .comments(new ArrayList<>())
                .lastBooking(null)
                .nextBooking(null)
                .build();
    }

    private BookingDto setBookingDto(UserDto userDto, ItemDto itemDto) {
        return BookingDto.builder()
                .id(1L)
                .booker(userDto)
                .item(itemDto)
                .start(LocalDateTime.of(2024, 7, 27, 0, 0))
                .end(LocalDateTime.of(2024, 8, 1, 0, 0))
                .status(BookingStatus.CANCELLED.toString())
                .bookingShortDto(null)
                .build();
    }

    private ItemDto setItemDto(long itemId, long ownerId) {
        return ItemDto.builder()
                .id(itemId)
                .name("TestItem")
                .description("Test description")
                .requestId(null)
                .available(true)
                .ownerId(ownerId)
                .comments(new ArrayList<>())
                .lastBooking(null)
                .nextBooking(null)
                .build();
    }

    private UserDto setUserDto(long id) {
        return UserDto.builder()
                .id(id)
                .name("TestUser")
                .email("TestUserEmail@test.com")
                .build();
    }
}