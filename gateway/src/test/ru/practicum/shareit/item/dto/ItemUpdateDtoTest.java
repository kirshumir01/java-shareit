package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.core.ResolvableType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemUpdateDtoTest {
    private final JacksonTester<ItemUpdateDto> json;

    @Test
    void deserializeItemUpdateDtoTest() throws Exception {
        long id = 1L;
        String name = "TestItem";
        String description = "Test description";
        boolean available = true;

        String incomingJson = String.format("{\"id\": \"%s\",\"name\": \"%s\",\"description\": \"%s\"," +
                "\"available\": \"%s\"}", id, name, description, available);

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto(id, name, description, available);
        ObjectContent<ItemUpdateDto> expectedObject = new ObjectContent<>(
                ResolvableType.forClass(ItemUpdateDto.class), itemUpdateDto);

        assertThat(json.parse(incomingJson))
                .usingRecursiveComparison()
                .isEqualTo(expectedObject);
    }

}