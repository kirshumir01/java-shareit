package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> getAll();

    UserDto get(long userId);

    UserDto update(UserDto newUserDto, long userId);

    void delete(long userId);
}