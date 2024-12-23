package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("Server: received request from userId = {} to create request: {}", userId, itemRequestCreateDto);
        return itemRequestService.create(userId, itemRequestCreateDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDtoWithAnswers> getAllUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Server: received request from userId = {} to get all own requests", userId);
        return itemRequestService.getAllUserRequests(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllRequestsExceptUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Server: received request from userId = {} to get all requests except own", userId);
        return itemRequestService.getAllRequestsExceptUser(userId);
    }


    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDtoWithAnswers getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PathVariable("requestId") long requestId) {
        log.info("Server: received request from userId = {} to get request by id = {}", userId, requestId);
        return itemRequestService.getRequestById(requestId);
    }
}