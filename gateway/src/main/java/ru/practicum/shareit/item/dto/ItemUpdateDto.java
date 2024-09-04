package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemUpdateDto {
    private Long id;
    @Length(max = 50)
    private String name;
    @Length(max = 200)
    private String description;
    private Boolean available;
}
