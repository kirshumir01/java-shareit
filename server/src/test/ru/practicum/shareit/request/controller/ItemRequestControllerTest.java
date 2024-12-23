package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService mockItemRequestService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;

    private final String header = "X-Sharer-User-Id";


    @Test
    void createItemRequestTest() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("Test description");
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Test description", LocalDateTime.now());

        when(mockItemRequestService.create(1L, itemRequestCreateDto)).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestDto.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));

        verify(mockItemRequestService, times(1)).create(anyLong(), any(ItemRequestCreateDto.class));
        verifyNoMoreInteractions(mockItemRequestService);
    }

    @Test
    void getAllUserRequestsTest() throws Exception {
        List<ItemShortDto> items1 = List.of(new ItemShortDto(1L, "item1", 1L));
        ItemRequestDtoWithAnswers itemRequest1 = new ItemRequestDtoWithAnswers(
                1L, "description", LocalDateTime.now(), items1);

        List<ItemShortDto> items2 = List.of(new ItemShortDto(2L, "item2", 2L));
        ItemRequestDtoWithAnswers itemRequest2 = new ItemRequestDtoWithAnswers(
                2L, "description", LocalDateTime.now(), items2);

        List<ItemRequestDtoWithAnswers> requests = List.of(itemRequest1, itemRequest2);

        when(mockItemRequestService.getAllUserRequests(anyLong())).thenReturn(requests);

        mvc.perform(get("/requests").header(header, anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(is(requests.size())));

        verify(mockItemRequestService, times(1)).getAllUserRequests(anyLong());
        verifyNoMoreInteractions(mockItemRequestService);
    }

    @Test
    void getAllRequestsExceptUserTest() throws Exception {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "Test description", LocalDateTime.now());
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "Test description", LocalDateTime.now());

        List<ItemRequestDto> requests = List.of(itemRequestDto1, itemRequestDto2);
        when(mockItemRequestService.getAllRequestsExceptUser(anyLong())).thenReturn(requests);

        mvc.perform(get("/requests/all").header(header, anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(is(requests.size())));

        verify(mockItemRequestService, times(1)).getAllRequestsExceptUser(anyLong());
        verifyNoMoreInteractions(mockItemRequestService);
    }

    @Test
    void getRequestByIdTest() throws Exception {
        long requestId = 1L;

        List<ItemShortDto> items = List.of(new ItemShortDto(1L, "item1", 1L));
        ItemRequestDtoWithAnswers itemRequest = new ItemRequestDtoWithAnswers(
                1L, "description", LocalDateTime.now(), items);

        when(mockItemRequestService.getRequestById(anyLong())).thenReturn(itemRequest);

        mvc.perform(get("/requests/" + requestId)
                        .header(header, anyLong()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(is(requestId), Long.class))
                .andExpect(jsonPath("$.description").value(is("description"), String.class));

        verify(mockItemRequestService, times(1)).getRequestById(requestId);
        verifyNoMoreInteractions(mockItemRequestService);
    }
}