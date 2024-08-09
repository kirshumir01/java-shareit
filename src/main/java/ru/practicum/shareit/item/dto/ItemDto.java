package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDataDto;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.validationgroups.Create;
import ru.practicum.shareit.validationgroups.Update;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Create.class})
    @Size(groups = {Create.class, Update.class}, min = 1)
    private String name;
    @NotBlank(groups = {Create.class})
    @Size(groups = {Create.class, Update.class}, min = 1)
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    private Long ownerId;
    private BookingDataDto lastBooking;
    private BookingDataDto nextBooking;
    private List<CommentOutputDto> comments;
}