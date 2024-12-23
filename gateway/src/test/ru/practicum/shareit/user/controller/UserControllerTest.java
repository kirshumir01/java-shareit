package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    UserClient mockUserClient;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createUserTest() throws Exception {
        UserCreateDto userCreateDto = setUserCreateDto();

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.CREATED);
        when(mockUserClient.create(userCreateDto)).thenReturn(response);

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isCreated());

        verify(mockUserClient, times(1)).create(any());
        verifyNoMoreInteractions(mockUserClient);
    }

    @Test
    void getUserTest() throws Exception {
        long id = 111L;

        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        when(mockUserClient.get(id)).thenReturn(response);

        mvc.perform(get("/users/" + id)).andExpect(status().isOk());

        verify(mockUserClient, times(1)).get(anyLong());
        verifyNoMoreInteractions(mockUserClient);
    }

    @Test
    void getAllUsers() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        when(mockUserClient.getAll()).thenReturn(response);

        mvc.perform(get("/users"))
                .andExpect(status().isOk());
        verify(mockUserClient, times(1)).getAll();
        verifyNoMoreInteractions(mockUserClient);
    }

    @Test
    void deleteUserTest() throws Exception {
        long id = 111L;

        mvc.perform(delete("/users/" + id))
                .andExpect(status().isNoContent());
        verify(mockUserClient, times(1)).delete(id);
        verifyNoMoreInteractions(mockUserClient);
    }

    @Test
    void updateUserTest() throws Exception {
        long id = 111L;

        UserUpdateDto userUpdateDto = setUserUpdateDto(id);

        String json = objectMapper.writeValueAsString(userUpdateDto);
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);

        when(mockUserClient.update(userUpdateDto)).thenReturn(response);

        mvc.perform(patch("/users/" + id)
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk());

        verify(mockUserClient, times(1)).update(any());
        verifyNoMoreInteractions(mockUserClient);
    }

    private UserCreateDto setUserCreateDto() {
        return UserCreateDto.builder()
                .name("TestUser")
                .email("TestUserEmail@test.com")
                .build();
    }

    private UserUpdateDto setUserUpdateDto(long id) {
        return UserUpdateDto.builder()
                .id(id)
                .name("NewTestUser")
                .email("NewTestUserEmail@test.com")
                .build();
    }
}