package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.baseclient.BaseClient;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> create(long userId, ItemCreateDto itemCreateDto) {
        String path = "";
        return post(path, userId, null, itemCreateDto);
    }

    public ResponseEntity<Object> get(long itemId, long userId) {
        String path = String.format("/%d", itemId);
        return get(path, userId, null);
    }

    public ResponseEntity<Object> getAllByOwnerId(long userId) {
        String path = "";
        return get(path, userId, null);
    }

    public ResponseEntity<Object> getByText(String text) {
        String path = "/search?text=";
        return get(path + text, null, null);
    }

    public ResponseEntity<Object> update(long userId, long itemId, ItemUpdateDto itemUpdateDto) {
        String path = String.format("/%d", itemId);
        return patch(path, userId, null, itemUpdateDto);
    }

    public ResponseEntity<Object> addComment(CommentCreateDto commentCreateDto, long itemId, long userId) {
        String path = String.format("/%d/comment", itemId);
        return post(path, userId, null, commentCreateDto);
    }
}