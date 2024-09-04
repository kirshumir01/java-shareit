package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    private final String header = "X-Sharer-User-Id";

    @Test
    void createItemTest() throws Exception {
        long userId = 222L;
        ItemCreateDto itemCreateDto = setItemCreateDto();
        ItemDto itemDto = setItemDto(333L);
        when(itemService.create(itemCreateDto, userId)).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header(header, 222L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
        verify(itemService, times(1)).create(any(), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemTest() throws Exception {
        long userId = 222L;
        long itemId = 111L;
        ItemDto itemDto = setItemDto(userId);

        when(itemService.get(itemId, userId)).thenReturn(itemDto);

        mvc.perform(get("/items/" + itemId)
                        .header(header, 222L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.comments[0].id", is(itemDto.getComments().getFirst().getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(itemDto.getComments().getFirst().getText()), String.class))
                .andExpect(jsonPath("$.comments[0].authorName", is(itemDto.getComments().getFirst().getAuthorName()), String.class))
                .andExpect(jsonPath("$.comments[0].created", is(itemDto.getComments().getFirst().getCreated().toString()), String.class))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.status", is(itemDto.getLastBooking().getStatus().toString()), String.class))
                .andExpect(jsonPath("$.lastBooking.start", is(itemDto.getLastBooking().getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.lastBooking.end", is(itemDto.getLastBooking().getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class));

        verify(itemService, times(1)).get(anyLong(), anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getAllItemsTest() throws Exception {
        long userId = 222L;

        ItemDto itemDto = setItemDto(userId);
        List<ItemDto> itemDtoList = List.of(itemDto);

        when(itemService.getAllByOwnerId(userId)).thenReturn(itemDtoList);

        mvc.perform(get("/items")
                        .header(header, 222L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].comments[0].id", is(itemDto.getComments().getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(itemDto.getComments().getFirst().getText()), String.class))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(itemDto.getComments().getFirst().getAuthorName()), String.class))
                .andExpect(jsonPath("$[0].comments[0].created", is(itemDto.getComments().getFirst().getCreated().toString()), String.class))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemDto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.status", is(itemDto.getLastBooking().getStatus().toString()), String.class))
                .andExpect(jsonPath("$[0].lastBooking.start", is(itemDto.getLastBooking().getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class))
                .andExpect(jsonPath("$[0].lastBooking.end", is(itemDto.getLastBooking().getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class));

        verify(itemService, times(1)).getAllByOwnerId(anyLong());
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void searchItemsTest() throws Exception {
        String text = "item";
        long userId = 222L;

        ItemDto itemDto = setItemDto(userId);
        List<ItemDto> itemDtoList = List.of(itemDto);

        when(itemService.getByText(text)).thenReturn(itemDtoList);

        mvc.perform(get("/items/search?text=" + text)
                        .header(header, 222L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].comments[0].id", is(itemDto.getComments().getFirst().getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(itemDto.getComments().getFirst().getText()), String.class))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(itemDto.getComments().getFirst().getAuthorName()), String.class))
                .andExpect(jsonPath("$[0].comments[0].created", is(itemDto.getComments().getFirst().getCreated().toString()), String.class))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemDto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.status", is(itemDto.getLastBooking().getStatus().toString()), String.class))
                .andExpect(jsonPath("$[0].lastBooking.start", is(itemDto.getLastBooking().getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class))
                .andExpect(jsonPath("$[0].lastBooking.end", is(itemDto.getLastBooking().getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class));

        verify(itemService, times(1)).getByText(text);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItem() throws Exception {
        long userId = 222L;
        long itemId = 111L;

        ItemUpdateDto itemUpdateDto = setItemUpdateDto(itemId, userId);
        ItemDto itemDto = setItemDto(userId);
        itemDto.setName("TestUpdateItem");
        itemDto.setDescription("Test update description");

        when(itemService.update(itemUpdateDto)).thenReturn(itemDto);

        mvc.perform(patch("/items/" + itemId)
                        .header(header, 222L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.comments[0].id", is(itemDto.getComments().getFirst().getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(itemDto.getComments().getFirst().getText()), String.class))
                .andExpect(jsonPath("$.comments[0].authorName", is(itemDto.getComments().getFirst().getAuthorName()), String.class))
                .andExpect(jsonPath("$.comments[0].created", is(itemDto.getComments().getFirst().getCreated().toString()), String.class))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.status", is(itemDto.getLastBooking().getStatus().toString()), String.class))
                .andExpect(jsonPath("$.lastBooking.start", is(itemDto.getLastBooking().getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.lastBooking.end", is(itemDto.getLastBooking().getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class));

        verify(itemService, times(1)).update(itemUpdateDto);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void createCommentTest() throws Exception {
        long userId = 222L;
        long itemId = 111L;
        String text = "Item created comment";

        CommentCreateDto commentCreateDto = new CommentCreateDto(text);
        CommentDto commentDto = setCommentDto(text);
        commentDto.setText(text);

        when(commentService.createComment(commentCreateDto, itemId, userId)).thenReturn(commentDto);

        mvc.perform(post("/items/" + itemId + "/comment")
                        .header(header, 222L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class));

        verify(commentService, times(1)).createComment(commentCreateDto, itemId, userId);
        verifyNoMoreInteractions(commentService);
    }

    private ItemDto setItemDto(long ownerId) {
        List<CommentDto> commentDtoList = List.of(
                new CommentDto(
                        1L,
                        "Test comment",
                        "Test author",
                        LocalDateTime.now())
        );

        return ItemDto.builder()
                .id(111L)
                .name("TestItem")
                .description("Test description")
                .requestId(1L)
                .available(true)
                .ownerId(ownerId)
                .comments(commentDtoList)
                .lastBooking(setBookingShortLastDto())
                .nextBooking(setBookingShortNextDto())
                .build();
    }

    private ItemCreateDto setItemCreateDto() {
        return ItemCreateDto.builder()
                .name("TestItem")
                .description("Test description")
                .requestId(1L)
                .available(true)
                .build();
    }

    private ItemUpdateDto setItemUpdateDto(long itemId, long ownerId) {
        return ItemUpdateDto.builder()
                .id(itemId)
                .name("TestUpdateItem")
                .description("Test update description")
                .available(true)
                .ownerId(ownerId)
                .build();
    }

    private BookingDto.BookingShortDto setBookingShortLastDto() {
        return BookingDto.BookingShortDto.builder()
                .id(1L)
                .bookerId(70L)
                .start(LocalDateTime.of(2024, 7, 27, 0, 0))
                .end(LocalDateTime.of(2024, 8, 1, 0, 0))
                .status(BookingStatus.CANCELLED)
                .build();
    }

    private BookingDto.BookingShortDto setBookingShortNextDto() {
        return BookingDto.BookingShortDto.builder()
                .id(2L)
                .bookerId(99L)
                .start(LocalDateTime.of(2024, 8, 20, 0, 0))
                .end(LocalDateTime.of(2024, 8, 21, 0, 0))
                .status(BookingStatus.CANCELLED)
                .build();
    }

    private CommentDto setCommentDto(String text) {
        return CommentDto.builder()
                .id(1L)
                .text(text)
                .authorName("Test author")
                .created(LocalDateTime.now())
                .build();
    }
}