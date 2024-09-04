package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemServiceIntegrationTest {
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User user;
    private User owner;
    private Item item;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("TestUser")
                .email("TestUserEmail@test.com")
                .build();

        owner = User.builder()
                .name("TestOwner")
                .email("TestOwnerEmail@test.com")
                .build();

        item = Item.builder()
                .name("TestItem")
                .description("Test description")
                .available(true)
                .owner(owner)
                .request(null)
                .build();

        request = ItemRequest.builder()
                .description("Test description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        userRepository.save(user);
        userRepository.save(owner);
        itemRepository.save(item);
        itemRequestRepository.save(request);
    }

    @Test
    void itemCreateTest() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("TestCreatedItem")
                .description("Test created item description")
                .available(true)
                .requestId(request.getId())
                .build();

        ItemDto createdItem = itemService.create(itemCreateDto, owner.getId());

        assertEquals("TestCreatedItem", createdItem.getName());
        assertEquals("Test created item description", createdItem.getDescription());
    }

    @Test
    void getAllItemsByOwnerIdTest() {
        List<ItemDto> items = itemService.getAllByOwnerId(owner.getId());
        assertThat(items).hasSize(1);
        assertThat(items.getFirst().getId()).isEqualTo(item.getId());
        assertEquals(items.getFirst().getName(), item.getName());
    }

    @Test
    void getItemByIdTest() {
        ItemDto itemDto = itemService.get(item.getId(), owner.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
    }

    @Test
    void itemCreateWithUnavailableRequestIdTest() {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("TestCreatedItem")
                .description("Test created item description")
                .available(true)
                .requestId(2L)
                .build();

        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () ->
                itemService.create(itemCreateDto, owner.getId()));
        assertEquals(String.format("Item request by id = %d not found", itemCreateDto.getRequestId()), thrown.getMessage());
    }

    @Test
    void updateItem() {
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .id(item.getId())
                .name("TestUpdatedItem")
                .description("Test updated item description")
                .available(true)
                .ownerId(owner.getId())
                .build();

        ItemDto updatedItem = itemService.update(itemUpdateDto);

        assertEquals("TestUpdatedItem", updatedItem.getName());
        assertEquals("Test updated item description", updatedItem.getDescription());
    }

    @Test
    void searchItemByTextTest() {
        List<ItemDto> itemDtoList = itemService.getByText("Test");

        assertThat(itemDtoList).hasSize(1);
        assertEquals(itemDtoList.getFirst().getName(), item.getName());
        assertEquals(itemDtoList.getFirst().getDescription(), item.getDescription());
    }
}