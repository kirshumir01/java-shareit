package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void checkItemModelTest() {
        Item item = new Item(1L, "TestItem", "Test description", true, new User(), new ItemRequest());
        assertEquals(1L, item.getId());
        assertEquals("TestItem", item.getName());
        assertEquals("Test description", item.getDescription());
    }
}