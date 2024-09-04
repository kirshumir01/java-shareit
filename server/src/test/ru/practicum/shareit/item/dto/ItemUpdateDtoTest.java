package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemUpdateDtoTest {
    private final JacksonTester<ItemUpdateDto> json;

    @Test
    void serializeItemCreateDtoTest() throws Exception {

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .name("TestItem")
                .description("Test description")
                .available(true)
                .ownerId(1L)
                .build();

        JsonContent<ItemUpdateDto> result = json.write(itemUpdateDto);

        assertThat(result).extractingJsonPathValue("$.name")
                .satisfies(name -> assertThat(name).isEqualTo(itemUpdateDto.getName()));

        assertThat(result).extractingJsonPathValue("$.description")
                .satisfies(description -> assertThat(description).isEqualTo(itemUpdateDto.getDescription()));
    }

    @Test
    void getterAndSetterTest() {
        String name = "TestItem";
        String description = "Test description";

        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName(name);
        itemUpdateDto.setDescription(description);

        assertEquals(name, itemUpdateDto.getName());
        assertEquals(description, itemUpdateDto.getDescription());
    }

    @Test
    void equalsAndHashCodeTest() {
        String name = "TestItem";
        String description = "Test description";

        ItemUpdateDto itemUpdateDto1 = new ItemUpdateDto(1L, name, description, true, 1L);
        ItemUpdateDto itemUpdateDto2 = new ItemUpdateDto(1L, name, description, true, 1L);
        ItemUpdateDto itemUpdateDto3 = new ItemUpdateDto(2L, name, description, true, 3L);
        ItemUpdateDto itemUpdateDto4 = new ItemUpdateDto(3L, name, description, true, 4L);

        assertEquals(itemUpdateDto1, itemUpdateDto2);
        assertNotEquals(itemUpdateDto1, itemUpdateDto3);
        assertNotEquals(itemUpdateDto1, itemUpdateDto4);
    }
}