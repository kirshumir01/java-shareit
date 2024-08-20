package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto create(@RequestBody @Valid UserCreateDto userCreateDto) {
        log.info("Запрос на сохранение информации о новом пользователе: POST /users");
        UserDto createdUser = userService.create(userCreateDto);
        log.info("Информация о новом пользователе {} сохранена", createdUser.getName());
        return createdUser;
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable long userId) {
        log.info("Запрос на получение информации о пользователе: GET /users/{}", userId);
        UserDto user = userService.get(userId);
        log.info("Получена информация о пользователе {}", userService.get(userId).getName());
        return user;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Запрос информации обо всех пользователях: GET /users");
        List<UserDto> users = userService.getAll();
        log.info("Получена информации обо всех {} пользователях", users.size());
        return users;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody @Valid UserUpdateDto userUpdateDto, @PathVariable long userId) {
        log.info("Запрос на обновление информации пользователя: PATCH /users/{}", userId);
        userUpdateDto.setId(userId);
        UserDto updatedUser = userService.update(userUpdateDto);
        log.info("Информация пользователя {} обновлена", updatedUser.getName());
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId) {
        log.info("Запрос на удаление информации о пользователе: DELETE /users/{}", userId);
        userService.delete(userId);
        log.info("Информация о пользователе c идентификатором {} удалена", userId);
    }
}