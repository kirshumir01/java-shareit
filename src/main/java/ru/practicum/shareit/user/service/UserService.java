package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto create(UserCreateDto userCreateDto);

    List<UserDto> getAll();

    UserDto get(long userId);

    UserDto update(UserUpdateDto userUpdateDto);

    void delete(long userId);
}