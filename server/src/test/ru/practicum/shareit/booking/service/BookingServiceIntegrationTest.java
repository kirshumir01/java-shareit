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
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

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
    void getAllByBookerCurrentTest() {
        Booking booking1 = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().plusDays(10))
                .status(BookingStatus.APPROVED)
                .build();

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<BookingDto> bookings = bookingService.getAllByBooker(user.getId(), BookingState.CURRENT);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking1.getId());
    }

    @Test
    void getAllByBookerPastTest() {
        Booking booking1 = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().minusDays(30))
                .end(LocalDateTime.now().minusDays(15))
                .status(BookingStatus.CANCELLED)
                .build();

        Booking booking2 = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().minusDays(14))
                .end(LocalDateTime.now().minusDays(7))
                .status(BookingStatus.CANCELLED)
                .build();

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<BookingDto> bookings = bookingService.getAllByBooker(user.getId(), BookingState.PAST);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking1.getId());
    }

    @Test
    void getAllByBookerАгегкуTest() {
        Booking booking1 = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().plusDays(30))
                .end(LocalDateTime.now().plusDays(45))
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().plusDays(60))
                .end(LocalDateTime.now().plusDays(80))
                .status(BookingStatus.APPROVED)
                .build();

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<BookingDto> bookings = bookingService.getAllByBooker(user.getId(), BookingState.FUTURE);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(booking2.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking1.getId());
    }

    @Test
    void getAllByBookerAllBookings() {
        Booking booking1 = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().plusDays(30))
                .end(LocalDateTime.now().plusDays(45))
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().minusDays(14))
                .end(LocalDateTime.now().minusDays(7))
                .status(BookingStatus.CANCELLED)
                .build();

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<BookingDto> bookings = bookingService.getAllByBooker(user.getId(), BookingState.ALL);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking2.getId());
    }


    @Test
    void getAllByOwnerAllBookingsTest() {
        Booking booking1 = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().plusDays(30))
                .end(LocalDateTime.now().plusDays(45))
                .status(BookingStatus.APPROVED)
                .build();

        Booking booking2 = Booking.builder()
                .booker(user)
                .item(item)
                .start(LocalDateTime.now().minusDays(14))
                .end(LocalDateTime.now().minusDays(7))
                .status(BookingStatus.CANCELLED)
                .build();

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<BookingDto> bookings = bookingService.getAllByOwner(owner.getId(), BookingState.ALL);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(booking1.getId());
        assertThat(bookings.get(1).getId()).isEqualTo(booking2.getId());
    }
}