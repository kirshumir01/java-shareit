package ru.practicum.shareit.item.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Spy
    private ItemClient itemClient = new ItemClient("http://localhost:9090", new RestTemplateBuilder());

    @Test
    void createItem() {
        long userId = 111L;
        ItemCreateDto itemCreateDto = setItemCreateDto();
        String path = "";
        assertThrows(Throwable.class, () -> itemClient.create(userId, itemCreateDto));
        assertThrows(Throwable.class, () -> itemClient.post(path, userId, null, itemCreateDto));
    }

    @Test
    void updateItem() {
        long userId = 111L;
        long itemId = 222L;
        String path = String.format("/%d", itemId);
        ItemUpdateDto itemUpdateDto = setItemUpdateDto(itemId);
        assertThrows(Throwable.class, () -> itemClient.update(userId, itemId, itemUpdateDto));
        assertThrows(Throwable.class, () -> itemClient.post(path, userId, null, itemUpdateDto));
    }

    @Test
    void getItem() {
        long userId = 111L;
        long itemId = 222L;
        String path = String.format("/%d", itemId);
        assertThrows(Throwable.class, () -> itemClient.get(itemId, userId));
        assertThrows(Throwable.class, () -> itemClient.get(path, userId, null));
    }

    @Test
    void getAllItems() {
        long userId = 111L;
        String path = "";
        assertThrows(Throwable.class, () -> itemClient.getAllByOwnerId(userId));
        assertThrows(Throwable.class, () -> itemClient.get(path, userId, null));
    }

    @Test
    void searchItems() {
        String text = "text";
        String path = "/search";
        assertThrows(Throwable.class, () -> itemClient.getByText(text));
        assertThrows(Throwable.class, () -> itemClient.get(path, null, null));
    }

    @Test
    void addComment() {
        long userId = 111L;
        long itemId = 222L;
        String text = "comment";
        CommentCreateDto commentCreateDto = new CommentCreateDto(text);
        String path = String.format("/%d/comment", itemId);
        assertThrows(Throwable.class, () -> itemClient.addComment(commentCreateDto, itemId, userId));
        assertThrows(Throwable.class, () -> itemClient.post(path, userId, null, commentCreateDto));
    }

    private ItemCreateDto setItemCreateDto() {
        return ItemCreateDto.builder()
                .name("TestItem")
                .description("Test description")
                .requestId(1L)
                .available(true)
                .build();
    }

    private ItemUpdateDto setItemUpdateDto(long id) {
        return ItemUpdateDto.builder()
                .id(id)
                .name("TestItem")
                .description("Test description")
                .available(true)
                .build();
    }
}