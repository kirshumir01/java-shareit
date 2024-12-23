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
class UserCreateDtoTest {
    private final JacksonTester<UserCreateDto> json;


    @Test
    void deserializeUserCreateDtoTest() throws Exception {
        String name = "TestName";
        String email = "TestUserEmail@test.com";
        String incomingJson = String.format("{\"name\": \"%s\",\"email\": \"%s\"}", name, email);

        UserCreateDto userCreateDto = new UserCreateDto(name, email);
        ObjectContent<UserCreateDto> expectedObject = new ObjectContent<>(
                ResolvableType.forClass(UserCreateDto.class), userCreateDto);

        assertThat(json.parse(incomingJson))
                .usingRecursiveComparison()
                .isEqualTo(expectedObject);
    }
}