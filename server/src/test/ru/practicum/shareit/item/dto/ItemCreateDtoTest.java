package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemCreateDtoTest {
    private final JacksonTester<ItemCreateDto> json;

    @Test
    void serializeItemCreateDtoTest() throws Exception {

        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("TestItem")
                .description("Test description")
                .requestId(1L)
                .available(true)
                .build();

        JsonContent<ItemCreateDto> result = json.write(itemCreateDto);

        assertThat(result).extractingJsonPathValue("$.name")
                .satisfies(name -> assertThat(name).isEqualTo(itemCreateDto.getName()));

        assertThat(result).extractingJsonPathValue("$.description")
                .satisfies(description -> assertThat(description).isEqualTo(itemCreateDto.getDescription()));
    }

    @Test
    void getterAndSetterTest() {
        String name = "TestItem";
        String description = "Test description";

        ItemCreateDto itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName(name);
        itemCreateDto.setDescription(description);

        assertEquals(name, itemCreateDto.getName());
        assertEquals(description, itemCreateDto.getDescription());
    }

    @Test
    void equalsAndHashCodeTest() {
        String name = "TestItem";
        String description = "Test description";

        ItemCreateDto itemCreateDto1 = new ItemCreateDto(name, description, true, 1L);
        ItemCreateDto itemCreateDto2 = new ItemCreateDto(name, description, true, 1L);
        ItemCreateDto itemCreateDto3 = new ItemCreateDto(name, description, true, 3L);
        ItemCreateDto itemCreateDto4 = new ItemCreateDto(name, description, true, 4L);

        assertEquals(itemCreateDto1, itemCreateDto2);
        assertNotEquals(itemCreateDto1, itemCreateDto3);
        assertNotEquals(itemCreateDto1, itemCreateDto4);
    }
}