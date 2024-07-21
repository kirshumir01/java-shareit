package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
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
        checkEmail(userDto);
        User createdUser = userRepository.create(userMapper.toUser(userDto));
        return userMapper.toUserDto(createdUser);
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
        User userFromMemory = userRepository.get(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", userId));
        });
        return userMapper.toUserDto(userFromMemory);
    }

    @Override
    public UserDto update(UserDto newUserDto, long userId) {
        User existentUser = userRepository.get(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", userId));
        });
        User userForUpdate = userMapper.toUser(newUserDto);

        checkUserId(userId);
        if (!existentUser.getEmail().equals(userForUpdate.getEmail())) {
            checkEmail(newUserDto);
        }

        existentUser.setName(Objects.requireNonNullElse(userForUpdate.getName(), existentUser.getName()));
        existentUser.setEmail(Objects.requireNonNullElse(userForUpdate.getEmail(), existentUser.getEmail()));

        return userMapper.toUserDto(userRepository.update(existentUser, userId));
    }

    @Override
    public void delete(long userId) {
        userRepository.delete(userId);
    }

    private void checkUserId(long id) {
        if (userRepository.get(id).isEmpty()) {
            throw new NotFoundException("Пользователь с идентификатором " + id + " не найден.");
        }
    }

    private void checkEmail(UserDto userDto) {
        List<UserDto> usersWithSameEmails = getAll()
                .stream()
                .filter(u -> u.getEmail().equals(userDto.getEmail()))
                .toList();

        if (!usersWithSameEmails.isEmpty()) {
            throw new ConflictException(String.format("Пользователь с адресом электронной %s почты существует.",
                    userDto.getEmail()));
        }
    }
}