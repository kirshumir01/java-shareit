package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemCreateDto itemCreateDto) {
        log.info("Server: received request to create new item '{}'", itemCreateDto);
        return itemService.create(itemCreateDto, userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(
            @PathVariable("itemId") long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Server: received request to get item by id = {} and userId = {}", itemId, userId);
        return itemService.get(itemId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Server: received request to get all items by ownerId = {}", userId);
        return itemService.getAllByOwnerId(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getByText(@RequestParam(name = "text") String text) {
        log.info("Server: received request to find items by text: {}", text);
        return itemService.getByText(text);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody ItemUpdateDto itemUpdateDto,
            @PathVariable("itemId") long itemId) {
        itemUpdateDto.setId(itemId);
        itemUpdateDto.setOwnerId(userId);
        log.info("Server: received request from userId = {} to update item by id = {}", userId, itemId);
        return itemService.update(itemUpdateDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentCreateDto commentCreateDto) {
        log.info("Server: received request to add comment '{}' from userId = {} to itemId = {}", userId, commentCreateDto, itemId);
        return commentService.createComment(commentCreateDto, itemId, userId);
    }
}