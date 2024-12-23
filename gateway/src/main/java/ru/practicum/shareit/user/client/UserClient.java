package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.baseclient.BaseClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Service
public class UserClient extends BaseClient {
    private static String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> create(UserCreateDto userCreateDto) {
        String path = "";
        return post("", null, null, userCreateDto);
    }

    public ResponseEntity<Object> get(long userId) {
        String path = String.format("/%d", userId);
        return get(path, null, null);
    }

    public ResponseEntity<Object> getAll() {
        String path = "";
        return get(path, null, null);
    }

    public ResponseEntity<Object> update(UserUpdateDto userUpdateDto) {
        String path = String.format("/%d", userUpdateDto.getId());
        return patch(path, null, null, userUpdateDto);
    }

    public ResponseEntity<Object> delete(long userId) {
        String path = String.format("/%d", userId);
        return delete(path);
    }
}