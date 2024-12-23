package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    BookingRepository mockBookingRepository;

    @Mock
    UserRepository mockUserRepository;

    @Mock
    ItemRepository mockItemRepository;

    @InjectMocks
    BookingServiceImpl bookingServiceImpl;

    @Test
    void createBookingTest() {
        long userId = 1L;
        long ownerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;

        User booker = setUser(userId);
        User owner = setOwner(ownerId);
        Item item = setItem(itemId, owner);

        UserDto userDto = setUserDto(userId);
        ItemDto itemDto = setItemDto(itemId, ownerId);

        BookingCreateDto bookingCreateDto = setBookingCreateDto(itemId);
        Booking booking = setBooking(bookingId, item, booker);
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        BookingDto bookingDtoMustReturned = setBookingDto(bookingId, itemDto, userDto);
        bookingDtoMustReturned.setStart(bookingCreateDto.getStart());
        bookingDtoMustReturned.setEnd(bookingCreateDto.getEnd());

        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booker));
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(mockBookingRepository.save(any(Booking.class))).thenReturn(booking);
        InOrder inOrder = inOrder(mockBookingRepository);

        BookingDto returnedBookingDto = bookingServiceImpl.create(userId, bookingCreateDto);

        inOrder.verify(mockBookingRepository, times(1)).findById(anyLong());
        inOrder.verify(mockBookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(mockBookingRepository);
        Assertions.assertEquals(bookingDtoMustReturned, returnedBookingDto);
    }

    @Test
    void setBookingStatusApprovedTest() {
        long userId = 1L;
        long ownerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        User booker = setUser(userId);
        User owner = setOwner(ownerId);
        Item item = setItem(itemId, owner);

        UserDto userDto = setUserDto(userId);
        ItemDto itemDto = setItemDto(itemId, ownerId);

        Booking booking = setBooking(bookingId, item, booker);
        Booking updatedBooking = setBooking(bookingId, item, booker);
        updatedBooking.setStatus(BookingStatus.APPROVED);
        BookingDto bookingDtoMustReturned = setBookingDto(bookingId, itemDto, userDto);
        bookingDtoMustReturned.setStatus(BookingStatus.APPROVED.toString());

        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockBookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);
        InOrder inOrder = inOrder(mockBookingRepository);

        BookingDto returnedBookingDto = bookingServiceImpl.approvedByOwner(ownerId, bookingId, approved);

        inOrder.verify(mockBookingRepository, times(1)).findById(anyLong());
        inOrder.verify(mockBookingRepository, times(1)).save(any(Booking.class));
        verifyNoMoreInteractions(mockBookingRepository);
        Assertions.assertEquals(bookingDtoMustReturned, returnedBookingDto);
    }

    @Test
    void setBookingStatusAlreadyBookedTest() {
        long userId = 1L;
        long ownerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        User booker = setUser(userId);
        User owner = setOwner(ownerId);
        Item item = setItem(itemId, owner);

        Booking booking = setBooking(bookingId, item, booker);
        booking.setStatus(BookingStatus.APPROVED);

        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        InOrder inOrder = inOrder(mockBookingRepository);

        BadRequestException thrown = Assertions.assertThrows(BadRequestException.class, () ->
                bookingServiceImpl.approvedByOwner(ownerId, bookingId, approved));
        Assertions.assertEquals("Booking status already set up: APPROVED.", thrown.getMessage());
        inOrder.verify(mockBookingRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockBookingRepository);
    }

    @Test
    void getBookingByIdByOwnerTest() {
        long userId = 1L;
        long ownerId = 1L;
        long itemId = 1L;
        long bookingId = 1L;

        User booker = setUser(userId);
        User owner = setOwner(ownerId);
        Item item = setItem(itemId, owner);

        UserDto userDto = setUserDto(userId);
        ItemDto itemDto = setItemDto(itemId, ownerId);

        Booking booking = setBooking(bookingId, item, booker);
        BookingDto bookingDtoMustReturned = setBookingDto(bookingId, itemDto, userDto);

        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        InOrder inOrder = inOrder(mockBookingRepository);

        BookingDto returnedBookingDto = bookingServiceImpl.getBookingByIdAndUserId(userId, bookingId);

        inOrder.verify(mockBookingRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockBookingRepository);
        Assertions.assertEquals(bookingDtoMustReturned, returnedBookingDto);
    }

    @Test
    void getBookingByIdNotByOwnerFailTest() {
        long userId = 1L;
        long ownerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;

        User booker = setUser(userId);
        User owner = setOwner(ownerId);
        Item item = setItem(itemId, owner);

        Booking booking = setBooking(bookingId, item, booker);
        booking.getBooker().setId(3L);

        when(mockBookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        InOrder inOrder = inOrder(mockBookingRepository);


        NotFoundException thrown = Assertions.assertThrows(NotFoundException.class, () ->
                bookingServiceImpl.getBookingByIdAndUserId(userId, bookingId));
        Assertions.assertEquals(String.format("Item or booking by user with id = %d not found", userId), thrown.getMessage());
        inOrder.verify(mockBookingRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockBookingRepository);
    }

    @Test
    void getAllByBookerTest() {
        long userId = 1L;
        long ownerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        BookingState state = BookingState.ALL;

        User booker = setUser(userId);
        User owner = setOwner(ownerId);
        Item item = setItem(itemId, owner);

        UserDto userDto = setUserDto(userId);
        ItemDto itemDto = setItemDto(itemId, ownerId);

        List<Booking> bookings = List.of(setBooking(bookingId, item, booker));
        List<BookingDto> bookingDtoMustReturnedList = List.of(setBookingDto(bookingId, itemDto, userDto));

        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockBookingRepository.findAllByBookerIdOrderByStartDesc(anyLong())).thenReturn(bookings);
        InOrder inOrder = inOrder(mockBookingRepository);

        List<BookingDto> returnedBookingDtoList = bookingServiceImpl.getAllByBooker(userId, state);

        inOrder.verify(mockBookingRepository, times(1)).findAllByBookerIdOrderByStartDesc(anyLong());
        verifyNoMoreInteractions(mockBookingRepository);
        Assertions.assertEquals(bookingDtoMustReturnedList, returnedBookingDtoList);
    }

    @Test
    void getAllByOwnerTest() {
        long userId = 1L;
        long ownerId = 2L;
        long itemId = 1L;
        long bookingId = 1L;
        BookingState state = BookingState.ALL;

        User booker = setUser(userId);
        User owner = setOwner(ownerId);
        Item item = setItem(itemId, owner);

        UserDto userDto = setUserDto(userId);
        ItemDto itemDto = setItemDto(itemId, ownerId);

        List<Booking> bookings = List.of(setBooking(bookingId, item, booker));
        List<BookingDto> bookingDtoMustReturnedList = List.of(setBookingDto(bookingId, itemDto, userDto));

        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockBookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong())).thenReturn(bookings);
        InOrder inOrder = inOrder(mockBookingRepository);

        List<BookingDto> returnedBookingDtoList = bookingServiceImpl.getAllByOwner(ownerId, state);

        inOrder.verify(mockBookingRepository, times(1)).findAllByItemOwnerIdOrderByStartDesc(anyLong());
        verifyNoMoreInteractions(mockBookingRepository);
        Assertions.assertEquals(bookingDtoMustReturnedList, returnedBookingDtoList);
    }

    private User setUser(long userId) {
        return User.builder()
                .id(userId)
                .name("TestUser")
                .email("TestUserEmail@test.com")
                .build();
    }

    private User setOwner(long ownerId) {
        return User.builder()
                .id(ownerId)
                .name("TestOwner")
                .email("TestOwnerEmail@test.com")
                .build();
    }

    private UserDto setUserDto(long userId) {
        return UserDto.builder()
                .id(userId)
                .name("TestUser")
                .email("TestUserEmail@test.com")
                .build();
    }

    private Item setItem(long itemId, User owner) {
        return Item.builder()
                .id(itemId)
                .name("TestItem")
                .description("Test item description")
                .owner(owner)
                .available(true)
                .build();
    }

    private ItemDto setItemDto(long itemId, long ownerId) {
        return ItemDto.builder()
                .id(itemId)
                .name("TestItem")
                .description("Test item description")
                .requestId(null)
                .available(true)
                .ownerId(ownerId)
                .comments(new ArrayList<>())
                .lastBooking(null)
                .nextBooking(null)
                .build();
    }

    private Booking setBooking(long bookingId, Item item, User booker) {
        return Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(booker)
                .start(null)
                .end(null)
                .status(BookingStatus.WAITING)
                .build();
    }

    private BookingCreateDto setBookingCreateDto(long itemId) {
        return BookingCreateDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusMinutes(1))
                .end(LocalDateTime.now().plusDays(7))
                .build();
    }

    private BookingDto setBookingDto(long bookingId, ItemDto itemDto, UserDto bookerDto) {
        return BookingDto.builder()
                .id(bookingId)
                .item(itemDto)
                .booker(bookerDto)
                .start(null)
                .end(null)
                .status(BookingStatus.WAITING.toString())
                .build();
    }
}