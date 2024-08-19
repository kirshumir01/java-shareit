package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

public interface CommentService {

    CommentDto createComment(CommentCreateDto commentCreateDto, long userId);
}
