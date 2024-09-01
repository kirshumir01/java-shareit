package ru.practicum.shareit.user.dto;

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
class UserCreateDtoTest {
    private final JacksonTester<UserCreateDto> json;

    @Test
    void serializeUserCreateDtoTest() throws Exception {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("TestUserCreateDtoName")
                .email("TestUserCreateDto@email.com")
                .build();

        JsonContent<UserCreateDto> result = json.write(userCreateDto);

        assertThat(result).extractingJsonPathValue("$.name")
                .satisfies(name -> assertThat(name).isEqualTo(userCreateDto.getName()));

        assertThat(result).extractingJsonPathValue("$.email")
                .satisfies(email -> assertThat(email).isEqualTo(userCreateDto.getEmail()));
    }

    @Test
    void getterAndSetterTest() {
        String name = "TestName";
        String email = "TestUserEmail@test.com";

        UserCreateDto dto = new UserCreateDto();
        dto.setName(name);
        dto.setEmail(email);

        assertEquals(name, dto.getName());
        assertEquals(email, dto.getEmail());
    }

    @Test
    void equalsAndHashCodeTest() {
        String name = "TestName";
        String email = "TestUserEmail@test.com";

        UserCreateDto userDto1 = new UserCreateDto(name, email);
        UserCreateDto userDto2 = new UserCreateDto(name, email);
        UserCreateDto userDto3 = new UserCreateDto(name, "TestUserEmail3@test.com");
        UserCreateDto userDto4 = new UserCreateDto("TestName4", email);

        assertEquals(userDto1, userDto2);
        assertNotEquals(userDto1, userDto3);
        assertNotEquals(userDto1, userDto4);
    }
}