package ru.practicum.shareit.user.dto;

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
class UserUpdateDtoTest {
    private final JacksonTester<UserUpdateDto> json;

    @Test
    void deserializeUserUpdateDtoTest() throws Exception {
        long id = 111L;
        String name = "TestName";
        String email = "TestUserEmail@test.com";
        String incomingJson = String.format("{\"id\": \"%d\", \"name\": \"%s\",\"email\": \"%s\"}", id, name, email);

        UserUpdateDto userUpdateDto = new UserUpdateDto(id, name, email);
        ObjectContent<UserUpdateDto> expectedObject = new ObjectContent<>(ResolvableType.forClass(UserUpdateDto.class), userUpdateDto);

        assertThat(json.parse(incomingJson))
                .usingRecursiveComparison()
                .isEqualTo(expectedObject);
    }
}