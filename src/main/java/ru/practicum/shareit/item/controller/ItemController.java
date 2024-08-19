package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
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
    @ResponseStatus(HttpStatus.OK)
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid ItemCreateDto itemCreateDto) {
        log.info("Запрос на сохранение информации о новой вещи: POST /items");
        ItemDto createdItem = itemService.create(itemCreateDto, userId);
        log.info("Информация о новой вещи {} сохранена", createdItem.getName());
        return createdItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(
            @PathVariable("itemId") long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение информации о вещи: GET /items/{}", itemId);
        ItemDto item = itemService.get(itemId, userId);
        log.info("Получена информация о вещи {}", item.getName());
        return item;
    }

    @GetMapping
    public List<ItemDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение информации о вещах пользователя с идентификатором {}: GET /items", userId);
        List<ItemDto> itemsByOwner = itemService.getAllByOwnerId(userId);
        log.info("Получена информация о вещах пользователя с идентификатором {}: {}", userId, itemsByOwner);
        return itemsByOwner;
    }

    @GetMapping("/search")
    public List<ItemDto> getByText(@RequestParam(name = "text") String text) {
        log.info("Запрос на поиск вещи по названию/описанию: GET /items/search ?text='{}'", text);
        List<ItemDto> itemsBySearch = itemService.getByText(text);
        log.info("Найдена информация о вещах: {}", itemsBySearch);
        return itemsBySearch;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid ItemUpdateDto itemUpdateDto,
            @PathVariable("itemId") long itemId) {
        log.info("Запрос на обновление вещи: PATCH /items/{}", itemId);
        itemUpdateDto.setId(itemId);
        itemUpdateDto.setOwnerId(userId);
        ItemDto updatedItem = itemService.update(itemUpdateDto);
        log.info("Информация о вещи {} обновлена", itemUpdateDto.getName());
        return updatedItem;
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(@RequestBody @Valid CommentCreateDto commentCreateDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId) {
        log.info("Запрос на создание комментария к вещи: POST /items/{}/comment", itemId);
        commentCreateDto.setItemId(itemId);
        CommentDto createdComment = commentService.createComment(commentCreateDto, userId);
        log.info("Комментарий к вещи с id = {} создан: ", itemId);
        log.info("Содержание комментария: {}", createdComment.toString());
        return createdComment;
    }
}