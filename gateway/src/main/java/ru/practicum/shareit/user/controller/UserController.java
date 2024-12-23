package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestBody @Valid UserCreateDto userCreateDto) {
        log.info("Gateway: received request to create new user '{}'", userCreateDto);
        return userClient.create(userCreateDto);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> get(@PathVariable long userId) {
        log.info("Gateway: received request to get user by id = {}", userId);
        return userClient.get(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAll() {
        log.info("Gateway: received request to get all users");
        return userClient.getAll();
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@PathVariable @NotNull long userId,
                                         @RequestBody @Valid UserUpdateDto userUpdateDto) {
        userUpdateDto.setId(userId);
        log.info("Gateway: received request to update user by id = {}", userId);
        return userClient.update(userUpdateDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NotNull long userId) {
        log.info("Gateway: received request to delete user by id = {}", userId);
        userClient.delete(userId);
    }
}