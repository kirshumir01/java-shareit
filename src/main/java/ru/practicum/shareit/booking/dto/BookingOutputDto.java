package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingOutputDto {
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime start;
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime end;
    private String status;
    private UserDto booker;
    private ItemDto item;
}