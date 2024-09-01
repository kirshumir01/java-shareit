package ru.practicum.shareit.user.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserClientTest {
    @Spy
    private UserClient userClient = new UserClient("http://localhost:9090", new RestTemplateBuilder());

    @Test
    void createUserTest() {
        String path = "";
        UserCreateDto userCreateDto = setUserCreateDto();
        assertThrows(Throwable.class, () -> userClient.create(userCreateDto));
        assertThrows(Throwable.class, () -> userClient.post(path, null, null, userCreateDto));
    }

    @Test
    void getUser() {
        long id = 111L;
        String path = String.format("/%d", id);
        assertThrows(Throwable.class, () -> userClient.get(id));
        assertThrows(Throwable.class, () -> userClient.get(path, null, null));
    }

    @Test
    void getAllUsers() {
        String path = "";
        assertThrows(Throwable.class, () -> userClient.getAll());
        assertThrows(Throwable.class, () -> userClient.get(path, null, null));
    }

    @Test
    void deleteUser() {
        long id = 111L;
        String path = String.format("/%d", id);
        assertThrows(Throwable.class, () -> userClient.delete(id));
        assertThrows(Throwable.class, () -> userClient.delete(path));
    }

    @Test
    void updateUser() {
        long id = 111L;
        String path = String.format("/%d", id);
        UserUpdateDto userUpdateDto = setUserUpdateDto(id);
        assertThrows(Throwable.class, () -> userClient.update(userUpdateDto));
        assertThrows(Throwable.class, () -> userClient.post(path, null, null, userUpdateDto));
    }

    private UserCreateDto setUserCreateDto() {
        return UserCreateDto.builder()
                .name("TestUser")
                .email("TestUserEmail@test.com")
                .build();
    }

    private UserUpdateDto setUserUpdateDto(long id) {
        return UserUpdateDto.builder()
                .id(id)
                .name("NewTestUser")
                .email("NewTestUserEmail@test.com")
                .build();
    }
}