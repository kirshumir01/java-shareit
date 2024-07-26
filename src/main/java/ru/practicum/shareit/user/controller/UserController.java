package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validationgroups.Create;
import ru.practicum.shareit.validationgroups.Update;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Запрос на сохранение информации о новом пользователе {}", userDto.getName());
        UserDto createdUser = userService.create(userDto);
        log.info("Информация о новом пользователе {} сохранена", createdUser.getName());
        return createdUser;
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable long id) {
        log.info("Запрос на получение информации о пользователе с идентификатором {}", id);
        UserDto user = userService.get(id);
        log.info("Получена информация о пользователе {}", userService.get(id).getName());
        return user;
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Запрос информации обо всех пользователях");
        List<UserDto> users = userService.getAll();
        log.info("Получена информации обо всех пользователях");
        return users;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable long userId) {
        log.info("Запрос на обновление информации пользователя {}", userDto.getName());
        userDto.setId(userId);
        UserDto updatedUser = userService.update(userDto);
        log.info("Информация пользователя {} обновлена", userDto.getName());
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId) {
        log.info("Запрос на удаление информации о пользователе c идентификатором {}", userId);
        userService.delete(userId);
        log.info("Информация о пользователе c идентификатором {} удалена", userId);
    }
}