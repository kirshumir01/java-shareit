package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        Set<String> emails = userRepository.findAll().stream().map(User::getEmail).collect(Collectors.toSet());

        if (emails.contains(userDto.getEmail())) {
            throw new ConflictException(String.format("Пользователь с адресом электронной %s почты существует.",
                    userDto.getEmail()));
        }

        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id %d не найден.", userId)));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(UserDto newUserDto) {
        User user = userRepository.findById(newUserDto.getId()).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id %d не найден.", newUserDto.getId())));
        User newUser = UserMapper.toUser(newUserDto);

        Set<String> emails = userRepository.findAll().stream().map(User::getEmail).collect(Collectors.toSet());

        if (emails.contains(newUser.getEmail()) && !user.getEmail().equals(newUser.getEmail())) {
            throw new ConflictException(String.format("Пользователь с адресом электронной %s почты существует.",
                    newUser.getEmail()));
        }

        newUser.setName(Objects.requireNonNullElse(newUserDto.getName(), user.getName()));
        newUser.setEmail(Objects.requireNonNullElse(newUserDto.getEmail(), user.getEmail()));
        return UserMapper.toUserDto(newUser);
    }

    @Override
    @Transactional
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }
}