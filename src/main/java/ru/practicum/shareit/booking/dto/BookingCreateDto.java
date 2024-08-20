package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
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
public class BookingCreateDto {
    @NotNull
    private Long itemId;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    @Future
    private LocalDateTime end;
}