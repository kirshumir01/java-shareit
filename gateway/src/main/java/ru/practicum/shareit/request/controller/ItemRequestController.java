package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.client.ItemRequestClient;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("Gateway: received request from userId = {} to create request: {}", userId, itemRequestCreateDto);
        return itemRequestClient.create(userId, itemRequestCreateDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUserRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: received request from userId = {} to get all own requests", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRequestsExceptUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: received request from userId = {} to get all requests except own", userId);
        return itemRequestClient.getAllRequestsExceptUser(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable("requestId") long requestId) {
        log.info("Gateway: received request from userId = {} to get request by id = {}", userId, requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}