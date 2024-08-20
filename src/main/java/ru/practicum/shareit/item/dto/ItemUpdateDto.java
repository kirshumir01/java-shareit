package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemUpdateDto {
    private Long id;
    @Size(max = 30)
    private String name;
    @Size(max = 200)
    private String description;
    private Boolean available;
    private Long ownerId;
}
