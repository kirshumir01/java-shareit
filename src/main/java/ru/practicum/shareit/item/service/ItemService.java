package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentInputDto;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto get(long itemId);

    List<ItemDto> getAllByOwnerId(long userId);

    List<ItemDto> getByText(String text);

    ItemDto update(ItemDto newItemDto);

    CommentOutputDto createComment(CommentInputDto commentInputDto, long userId, long itemId);

    List<CommentOutputDto> getAllCommentsByItemId(long itemId);
}