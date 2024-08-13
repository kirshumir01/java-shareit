package ru.practicum.shareit.item.comment.mapper;

import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;

public class CommentMapper {

    public static CommentOutputDto toCommentOutputDto(Comment comment) {
        if (comment == null) return null;
        return CommentOutputDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentOutputDto> toCommentOutputDtos(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentOutputDto)
                .toList();
    }
}