package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.create(userMapper.toUser(userDto));
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto get(long userId) {
        User user = userRepository.get(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", userId));
        });
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto update(UserDto newUserDto) {
        User user = userRepository.get(newUserDto.getId()).orElseThrow(() -> {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", newUserDto.getId()));
        });

        User newUser = userRepository.update(userMapper.toUser(newUserDto));

        newUser.setName(Objects.requireNonNullElse(newUserDto.getName(), user.getName()));
        newUser.setEmail(Objects.requireNonNullElse(newUserDto.getEmail(), user.getEmail()));
        return userMapper.toUserDto(newUser);
    }

    @Override
    public void delete(long userId) {
        userRepository.delete(userId);
    }
}