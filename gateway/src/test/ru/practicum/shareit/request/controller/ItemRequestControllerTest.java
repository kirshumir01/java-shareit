package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestClient mockItemRequestClient;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String header = "X-Sharer-User-Id";

    @Test
    void createItemRequestTest() throws Exception {
        long userId = 1L;

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto("Test description");

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.CREATED);
        when(mockItemRequestClient.create(userId, itemRequestCreateDto)).thenReturn(response);

        mvc.perform(post("/requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestCreateDto))
                        .header(header, userId))
                .andExpect(status().isCreated());

        verify(mockItemRequestClient, times(1)).create(anyLong(), any(ItemRequestCreateDto.class));
        verifyNoMoreInteractions(mockItemRequestClient);
    }

    @Test
    void getUserRequestsTest() throws Exception {
        long userId = 1L;

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        when(mockItemRequestClient.getUserRequests(userId)).thenReturn(response);

        mvc.perform(get("/requests").header(header, userId))
                .andExpect(status().isOk());

        verify(mockItemRequestClient, times(1)).getUserRequests(userId);
        verifyNoMoreInteractions(mockItemRequestClient);
    }

    @Test
    void getAllRequestsExceptUserTest() throws Exception {
        long userId = 1L;

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        when(mockItemRequestClient.getAllRequestsExceptUser(userId)).thenReturn(response);

        mvc.perform(get("/requests/all").header(header, userId))
                .andExpect(status().isOk());

        verify(mockItemRequestClient, times(1)).getAllRequestsExceptUser(userId);
        verifyNoMoreInteractions(mockItemRequestClient);
    }

    @Test
    void getRequestByIdTest() throws Exception {
        long userId = 1L;
        long requestId = 1L;

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        when(mockItemRequestClient.getRequestById(userId, requestId)).thenReturn(response);

        mvc.perform(get("/requests/" + requestId)
                        .header(header, userId))
                .andExpect(status().isOk());

        verify(mockItemRequestClient, times(1)).getRequestById(userId, requestId);
        verifyNoMoreInteractions(mockItemRequestClient);
    }
}