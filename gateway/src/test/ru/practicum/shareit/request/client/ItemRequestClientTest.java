package ru.practicum.shareit.request.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {
    @Spy
    private ItemRequestClient itemRequestClient =
            new ItemRequestClient("http://localhost:9090", new RestTemplateBuilder());

    @Test
    void createItemRequestTest() {
        long userId = 1L;
        String path = "";

        ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto();
        assertThrows(Throwable.class, () -> itemRequestClient.create(userId, itemRequestCreateDto));
        assertThrows(Throwable.class, () -> itemRequestClient.post(path, userId, null, itemRequestCreateDto));
    }

    @Test
    void getUserRequestsTest() {
        long userId = 1L;
        String path = "";

        assertThrows(Throwable.class, () -> itemRequestClient.getUserRequests(userId));
        assertThrows(Throwable.class, () -> itemRequestClient.get(path, userId, null));
    }

    @Test
    void getAllRequestsExceptUserTest() {
        long userId = 1L;
        String path = "/all";

        assertThrows(Throwable.class, () -> itemRequestClient.getAllRequestsExceptUser(userId));
        assertThrows(Throwable.class, () -> itemRequestClient.get(path, userId, null));
    }

    @Test
    void getRequestByIdTest() {
        long userId = 1L;
        long requestId = 1L;
        String path = String.format("/%d", requestId);

        assertThrows(Throwable.class, () -> itemRequestClient.getAllRequestsExceptUser(requestId));
        assertThrows(Throwable.class, () -> itemRequestClient.get(path, userId, null));
    }
}