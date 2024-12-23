package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid ItemCreateDto itemCreateDto) {
        log.info("Gateway: received request to create new item '{}'", itemCreateDto);
        return itemClient.create(userId, itemCreateDto);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemById(
            @PathVariable("itemId") long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: received request to get item by id = {} and userId = {}", itemId, userId);
        return itemClient.get(itemId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Gateway: received request to get all items by ownerId = {}", userId);
        return itemClient.getAllByOwnerId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getByText(@RequestParam(name = "text") String text) {
        log.info("Gateway: received request to find items by text: {}", text);
        return itemClient.getByText(text);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId,
            @RequestBody @Valid ItemUpdateDto itemUpdateDto) {
        log.info("Gateway: received request from userId = {} to update item by id = {}", userId, itemId);
        return itemClient.update(userId, itemId, itemUpdateDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Gateway: received request to add comment '{}' from userId = {} to itemId = {}", userId, commentCreateDto, itemId);
        return itemClient.addComment(commentCreateDto, itemId, userId);
    }
}