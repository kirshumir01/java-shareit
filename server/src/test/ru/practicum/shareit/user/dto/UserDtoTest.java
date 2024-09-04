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
class UserDtoTest {
    private final JacksonTester<UserDto> json;

    @Test
    void serializeUserDtoTest() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("TestUserDtoName")
                .email("TestUserDto@Email.com")
                .build();

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(userDto.getId()));

        assertThat(result).extractingJsonPathValue("$.name")
                .satisfies(name -> assertThat(name).isEqualTo(userDto.getName()));

        assertThat(result).extractingJsonPathValue("$.email")
                .satisfies(email -> assertThat(email).isEqualTo(userDto.getEmail()));
    }

    @Test
    void getterAndSetterTest() {
        String name = "TestName";
        String email = "TestUserEmail@test.com";

        UserDto dto = new UserDto();
        dto.setName(name);
        dto.setEmail(email);

        assertEquals(name, dto.getName());
        assertEquals(email, dto.getEmail());
    }

    @Test
    void equalsAndHashCodeTest() {
        String name = "TestName";
        String email = "TestUserEmail@test.com";

        UserDto userDto1 = new UserDto(1L, name, email);
        UserDto userDto2 = new UserDto(1L, name, email);
        UserDto userDto3 = new UserDto(1L, name, "TestUserEmail3@test.com");
        UserDto userDto4 = new UserDto(1L, "TestName4", email);

        assertEquals(userDto1, userDto2);
        assertNotEquals(userDto1, userDto3);
        assertNotEquals(userDto1, userDto4);
    }
}