package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateDto {
    @NotBlank
    @Length(max = 50)
    private String name;
    @NotBlank
    @Length(max = 200)
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
