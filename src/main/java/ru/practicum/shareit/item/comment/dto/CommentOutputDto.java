package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentOutputDto {
    private Long id;
    private String text;
    private String authorName;
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime created;
}