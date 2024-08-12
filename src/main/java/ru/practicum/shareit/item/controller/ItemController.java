package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentInputDto;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validationgroups.Create;
import ru.practicum.shareit.validationgroups.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ItemDto create(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Запрос на сохранение информации о новом вещи {}", itemDto.getName());
        ItemDto createdItem = itemService.create(itemDto, userId);
        log.info("Информация о новой вещи {} сохранена", createdItem.getName());
        return createdItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") long itemId) {
        log.info("Запрос на получение информации о вещи с идентификатором {}", itemId);
        ItemDto itemDto = itemService.get(itemId);
        log.info("Получена информация о вещи {}", itemDto.getName());
        return itemDto;
    }

    @GetMapping
    public List<ItemDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрос на получение информации о вещах пользователя с идентификатором {}", userId);
        List<ItemDto> itemsByUser = itemService.getAllByOwnerId(userId);
        log.info("Получена информация о вещах пользователя с идентификатором {}: {}", userId, itemsByUser);
        return itemsByUser;
    }

    @GetMapping("/search")
    public List<ItemDto> getByText(@RequestParam(name = "text") String text) {
        log.info("Запрос на поиск вещи по названию/описанию: '{}'", text);
        List<ItemDto> itemsBySearch = itemService.getByText(text);
        log.info("Получена информация о вещах: {}", itemsBySearch);
        return itemsBySearch;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Validated({Update.class}) @RequestBody ItemDto itemDto,
            @PathVariable("itemId") long itemId) {
        log.info("Запрос на обновление информации о вещи {}", itemDto.getName());
        itemDto.setId(itemId);
        itemDto.setOwnerId(userId);
        ItemDto updatedItem = itemService.update(itemDto);
        log.info("Информация о вещи {} обновлена", itemDto.getName());
        return updatedItem;
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentOutputDto addComment(@Validated({Create.class}) @RequestBody CommentInputDto commentInputDto,
                                       @RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long itemId) {
        log.info("Запрос на создание комментария к вещи {}", itemService.get(itemId).getName());
        CommentOutputDto createdComment = itemService.createComment(commentInputDto, userId, itemId);
        log.info("Комментарий к вещи с id = {} создан: ", itemId);
        log.info("Содержание комментария: {}", createdComment.toString());
        return createdComment;
    }
}