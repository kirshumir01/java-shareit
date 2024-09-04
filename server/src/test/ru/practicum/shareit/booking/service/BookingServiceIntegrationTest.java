package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class BookingServiceIntegrationTest {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;

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

        booking = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        userRepository.save(user);
        userRepository.save(owner);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    void createBookingTest() {
        User newOwner = User.builder()
                .name("NewTestOwner")
                .email("NewTestOwnerEmail@test.com")
                .build();

        Item newItem = Item.builder()
                .name("NewTestItem")
                .description("New test description")
                .available(true)
                .owner(newOwner)
                .request(null)
                .build();

        userRepository.save(newOwner);
        itemRepository.save(newItem);

        BookingCreateDto bookingCreateDto = BookingCreateDto.builder()
                .itemId(newItem.getId())
                .start(LocalDateTime.now().plusMinutes(15))
                .end(LocalDateTime.now().plusDays(7))
                .build();

        BookingDto createdBookingDto = bookingService.create(user.getId(), bookingCreateDto);

        Assertions.assertThat(createdBookingDto.getBooker().getName()).isEqualTo(user.getName());
        Assertions.assertThat(createdBookingDto.getItem().getName()).isEqualTo(newItem.getName());
    }

    @Test
    void approvedByOwnerTest() {
    }

    @Test
    void getBookingByIdAndUserIdTest() {
    }

    @Test
    void getAllByBookerTest() {
    }

    @Test
    void getAllByOwnerTest() {
    }
}