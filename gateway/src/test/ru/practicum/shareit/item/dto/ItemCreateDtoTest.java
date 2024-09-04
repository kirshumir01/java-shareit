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
class ItemCreateDtoTest {
    private final JacksonTester<ItemCreateDto> json;

    @Test
    void deserializeItemCreateDtoTest() throws Exception {
        String name = "TestItem";
        String description = "Test description";
        boolean available = true;
        long requestId = 1L;

        String incomingJson = String.format("{\"name\": \"%s\",\"description\": \"%s\"," +
                "\"available\": \"%s\",\"requestId\": \"%s\"}", name, description, available, requestId);

        ItemCreateDto itemCreateDto = new ItemCreateDto(name, description, available, requestId);
        ObjectContent<ItemCreateDto> expectedObject = new ObjectContent<>(
                ResolvableType.forClass(ItemCreateDto.class), itemCreateDto);

        assertThat(json.parse(incomingJson))
                .usingRecursiveComparison()
                .isEqualTo(expectedObject);
    }
}