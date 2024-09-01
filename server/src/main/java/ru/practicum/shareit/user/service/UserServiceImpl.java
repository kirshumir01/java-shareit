package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
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
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDto create(UserCreateDto userCreateDto) {
        Set<String> emails = userRepository.findAll().stream().map(User::getEmail).collect(Collectors.toSet());

        if (emails.contains(userCreateDto.getEmail())) {
            throw new ConflictException(String.format("User with same email '%s' exists", userCreateDto.getEmail()));
        }

        User user = userRepository.save(UserMapper.toUser(userCreateDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public UserDto get(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User with id = %d not found", userId)));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<UserDto> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDto update(UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(userUpdateDto.getId()).orElseThrow(
                () -> new NotFoundException(String.format("User with id = %d not found", userUpdateDto.getId())));

        Set<String> emails = userRepository.findAll().stream().map(User::getEmail).collect(Collectors.toSet());

        if (emails.contains(userUpdateDto.getEmail()) && !user.getEmail().equals(userUpdateDto.getEmail())) {
            throw new ConflictException(String.format("User with same email '%s' exists", userUpdateDto.getEmail()));
        }

        user.setName(Objects.requireNonNullElse(userUpdateDto.getName(), user.getName()));
        user.setEmail(Objects.requireNonNullElse(userUpdateDto.getEmail(), user.getEmail()));
        user.setId(null);
        User updatedUser = userRepository.save(user);
        updatedUser.setId(userUpdateDto.getId());
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }
}