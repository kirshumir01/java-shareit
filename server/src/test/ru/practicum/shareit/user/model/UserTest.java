package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void checkUserModelTest() {
        User user = new User(1L, "TestUser", "TestUserEmail@test.com");
        assertEquals(1L, user.getId());
        assertEquals("TestUser", user.getName());
        assertEquals("TestUserEmail@test.com", user.getEmail());
    }
}