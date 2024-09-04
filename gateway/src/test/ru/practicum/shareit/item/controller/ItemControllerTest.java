package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    ItemClient mockItemClient;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    private final String header = "X-Sharer-User-Id";

    @Test
    void createItemTest() throws Exception {
        long userId = 111L;
        ItemCreateDto itemCreateDto = setItemCreateDto();

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.CREATED);
        when(mockItemClient.create(userId, itemCreateDto)).thenReturn(response);

        mvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header(header, userId))
                .andExpect(status().isCreated());

        verify(mockItemClient, times(1)).create(anyLong(), any());
        verifyNoMoreInteractions(mockItemClient);
    }

    @Test
    void getItemTest() throws Exception {
        long userId = 222L;
        long itemId = 111L;

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        when(mockItemClient.get(itemId, userId)).thenReturn(response);

        mvc.perform(get("/items/" + itemId)
                        .contentType("application/json")
                        .header(header, userId))
                .andExpect(status().isOk());

        verify(mockItemClient, times(1)).get(anyLong(), anyLong());
        verifyNoMoreInteractions(mockItemClient);
    }

    @Test
    void getAllItemsTest() throws Exception {
        long userId = 222L;

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        when(mockItemClient.getAllByOwnerId(userId)).thenReturn(response);

        mvc.perform(get("/items")
                        .contentType("application/json")
                        .header(header, userId))
                .andExpect(status().isOk());

        verify(mockItemClient, times(1)).getAllByOwnerId(anyLong());
        verifyNoMoreInteractions(mockItemClient);
    }

    @Test
    void updateItemTest() throws Exception {
        long userId = 111L;
        long itemId = 222L;

        ItemUpdateDto itemUpdateDto = setItemUpdateDto(itemId);

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        when(mockItemClient.update(userId, itemId, itemUpdateDto)).thenReturn(response);

        mvc.perform(patch("/items/" + itemId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemUpdateDto))
                        .header(header, userId))
                .andExpect(status().isOk());

        verify(mockItemClient, times(1)).update(anyLong(), anyLong(), any());
        verifyNoMoreInteractions(mockItemClient);
    }

    @Test
    void searchItems() throws Exception {
        long userId = 111L;
        String text = "text";

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        when(mockItemClient.getByText(text)).thenReturn(response);

        mvc.perform(get("/items/search?text=" + text).header(header, userId)).andExpect(status().isOk());

        verify(mockItemClient, times(1)).getByText(text);
        verifyNoMoreInteractions(mockItemClient);
    }

    @Test
    void addComment() throws Exception {
        long userId = 111L;
        long itemId = 222L;
        String text = "comment";

        CommentCreateDto commentCreateDto = new CommentCreateDto(text);

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.CREATED);
        when(mockItemClient.addComment(commentCreateDto, itemId, userId)).thenReturn(response);

        mvc.perform(post("/items/" + itemId + "/comment")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .header(header, userId))
                .andExpect(status().isCreated());

        verify(mockItemClient, times(1)).addComment(any(), anyLong(), anyLong());
        verifyNoMoreInteractions(mockItemClient);
    }

    private ItemCreateDto setItemCreateDto() {
        return ItemCreateDto.builder()
                .name("TestItem")
                .description("Test description")
                .requestId(1L)
                .available(true)
                .build();
    }

    private ItemUpdateDto setItemUpdateDto(long itemId) {
        return ItemUpdateDto.builder()
                .id(itemId)
                .name("TestUpdateItem")
                .description("Test update description")
                .available(true)
                .build();
    }
}