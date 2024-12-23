package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;

    @InjectMocks
    UserServiceImpl userServiceImpl;

    @Test
    void createUserTest() {
        User user = setUser();
        UserCreateDto userCreateDto = setUserCreateDto();
        UserDto userDtoMustReturned = setUserDto(user.getId());

        when(mockUserRepository.save(any(User.class))).thenReturn(user);
        InOrder inOrder = inOrder(mockUserRepository);

        UserDto returnedUserDto = userServiceImpl.create(userCreateDto);

        inOrder.verify(mockUserRepository, times(1)).findAll();
        inOrder.verify(mockUserRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(mockUserRepository);
        Assertions.assertEquals(userDtoMustReturned, returnedUserDto);
    }

    @Test
    void getExistentUserTest() {
        User user = setUser();
        UserDto userDto = setUserDto(user.getId());

        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(user));
        InOrder inOrder = inOrder(mockUserRepository);

        UserDto returnedUserDto = userServiceImpl.get(user.getId());

        Assertions.assertEquals(userDto, returnedUserDto);
        inOrder.verify(mockUserRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    void getNonExistingUser() {
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userServiceImpl.get(anyLong()));
        verify(mockUserRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    void getAllUsersTest() {
        User user1 = new User(1L, "TestUser1", "TestUserEmail1@test.com");
        User user2 = new User(2L, "TestUser2", "TestUserEmail2@test.com");
        User user3 = new User(3L, "TestUser3", "TestUserEmail3@test.com");
        UserDto userDto1 = new UserDto(1L, "TestUser1", "TestUserEmail1@test.com");
        UserDto userDto2 = new UserDto(2L, "TestUser2", "TestUserEmail2@test.com");
        UserDto userDto3 = new UserDto(3L, "TestUser3", "TestUserEmail3@test.com");

        List<User> users = List.of(user1, user2, user3);
        List<UserDto> userDtoList = List.of(userDto1, userDto2, userDto3);

        when(mockUserRepository.findAll()).thenReturn(users);

        List<UserDto> returnedUserDto = userServiceImpl.getAll();

        verify(mockUserRepository, times(1)).findAll();
        verifyNoMoreInteractions(mockUserRepository);
        Assertions.assertTrue(returnedUserDto.containsAll(userDtoList));
    }

    @Test
    void getAllReturnsEmptyListTest() {
        when(mockUserRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> returnedUserDto = userServiceImpl.getAll();

        verify(mockUserRepository, times(1)).findAll();
        verifyNoMoreInteractions(mockUserRepository);
        Assertions.assertTrue(returnedUserDto.isEmpty());
    }

    @Test
    void updateUserTest() {
        String newName = "NewTestUser";
        String newEmail = "NewTestUserEmail@test.com";

        User oldUser = setUser();
        User newUser = setUser();
        newUser.setName(newName);
        newUser.setEmail(newEmail);

        UserUpdateDto userUpdateDto = setUserUpdateDto(oldUser.getId());

        UserDto userDtoMustReturned = setUserDto(oldUser.getId());
        userDtoMustReturned.setName(newName);
        userDtoMustReturned.setEmail(newEmail);

        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(oldUser));
        when(mockUserRepository.save(any(User.class))).thenReturn(newUser);
        InOrder inOrder = inOrder(mockUserRepository);

        UserDto returnedUserDto = userServiceImpl.update(userUpdateDto);

        inOrder.verify(mockUserRepository, times(1)).findById(anyLong());
        inOrder.verify(mockUserRepository, times(1)).findAll();
        inOrder.verify(mockUserRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(mockUserRepository);
        Assertions.assertEquals(userDtoMustReturned, returnedUserDto);
    }

    @Test
    void updateUserWithIncorrectIdTest() {
        UserUpdateDto userUpdateDto = setUserUpdateDto(111L);

        InOrder inOrder = inOrder(mockUserRepository);

        Assertions.assertThrows(NotFoundException.class, () -> userServiceImpl.update(userUpdateDto));
        inOrder.verify(mockUserRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockUserRepository);
    }

    @Test
    void deleteUserTest() {
        doNothing().when(mockUserRepository).deleteById(anyLong());
        InOrder inOrder = inOrder(mockUserRepository);

        userServiceImpl.delete(1L);

        inOrder.verify(mockUserRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(mockUserRepository);
    }

    private User setUser() {
        return User.builder()
                .id(1L)
                .name("TestUser")
                .email("TestUserEmail@test.com")
                .build();
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

    private UserDto setUserDto(long id) {
        return UserDto.builder()
                .id(id)
                .name("TestUser")
                .email("TestUserEmail@test.com")
                .build();
    }
}