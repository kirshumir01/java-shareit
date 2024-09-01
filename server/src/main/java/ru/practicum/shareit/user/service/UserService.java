package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto create(UserCreateDto userCreateDto);

    UserDto get(long userId);

    List<UserDto> getAll();

    UserDto update(UserUpdateDto userUpdateDto);

    void delete(long userId);
}