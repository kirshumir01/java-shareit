package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class UserServiceIntegrationTest {
    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Test user")
                .email("TestUserEmail@test.com")
                .build();
    }

    @Test
    void createUserTest() {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("Test user")
                .email("TestUserEmail@test.com")
                .build();

        UserDto createdUser = userService.create(userCreateDto);

        assertEquals(userCreateDto.getEmail(), createdUser.getEmail());
        assertEquals(userCreateDto.getName(), userCreateDto.getName());
    }

    @Test
    void getUserTest() {
        userRepository.save(user);

        UserDto returnedUser = userService.get(user.getId());

        assertEquals(user.getId(), returnedUser.getId());
        assertEquals(user.getName(), returnedUser.getName());
        assertEquals(user.getEmail(), returnedUser.getEmail());
    }

    @Test
    void getAll() {
        User user1 = userRepository.save(User.builder().name("User1").email("user1@email.com").build());
        User user2 = userRepository.save(User.builder().name("User2").email("user2@email.com").build());
        User user3 = userRepository.save(User.builder().name("User3").email("user3@email.com").build());

        List<UserDto> returnedUsers = userService.getAll();

        assertThat(returnedUsers).hasSize(3);
        assertEquals(user1.getId(), returnedUsers.get(0).getId());
        assertEquals(user3.getName(), returnedUsers.get(2).getName());
        assertEquals(user2.getEmail(), returnedUsers.get(1).getEmail());
    }

    @Test
    void update() {
        User oldUser = userRepository.save(user);

        UserUpdateDto userUpdateDto = new UserUpdateDto(user.getId(), "New name", "NewEmail@test.com");

        UserDto updatedUser = userService.update(userUpdateDto);

        assertEquals(oldUser.getId(), updatedUser.getId());
        assertEquals(oldUser.getName(), updatedUser.getName());
        assertEquals(oldUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void delete() {
        userRepository.save(user);

        userService.delete(user.getId());

        assertThat(userRepository.findById(user.getId()).isEmpty());
    }
}