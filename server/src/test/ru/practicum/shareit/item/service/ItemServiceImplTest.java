package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.comment.service.CommentServiceImpl;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private CommentRepository mockCommentRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private BookingRepository mockBookingRepository;

    @InjectMocks
    ItemServiceImpl itemServiceImpl;

    @Test
    void itemCreateTest() {
        User owner = setOwner();
        Item item = setItem(owner);

        ItemCreateDto itemCreateDto = setItemCreateDto();
        ItemDto itemDtoMustReturned = setItemDto(item.getId(), owner.getId());

        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(mockItemRepository.save(any(Item.class))).thenReturn(item);
        InOrder inOrder = inOrder(mockItemRepository);

        ItemDto returnedItemDto = itemServiceImpl.create(itemCreateDto, owner.getId());

        inOrder.verify(mockItemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(mockItemRepository);
        Assertions.assertEquals(itemDtoMustReturned, returnedItemDto);
    }


    @Test
    void getItemByItemIdAndUserIdNotOwnerTest() {
        User user = setUser();
        User owner = setOwner();
        Item item = setItem(owner);

        Comment comment = setComment(user, item);
        Booking lastBooking = setLastBooking(user, item);
        Booking nextBooking = setNextBooking(user, item);

        ItemDto itemDtoMustReturned = setItemDto(item.getId(), owner.getId());
        itemDtoMustReturned.setComments(List.of(CommentMapper.toCommentDto(comment)));

        ItemDto itemDtoMustNotBeReturned = setItemDto(item.getId(), owner.getId());
        itemDtoMustNotBeReturned.setComments(List.of(CommentMapper.toCommentDto(comment)));
        itemDtoMustNotBeReturned.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
        itemDtoMustNotBeReturned.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));

        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mockCommentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));
        InOrder inOrder = inOrder(mockItemRepository);

        ItemDto returnedItemDto = itemServiceImpl.get(item.getId(), user.getId());

        inOrder.verify(mockItemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockItemRepository);
        Assertions.assertEquals(itemDtoMustReturned, returnedItemDto);
        Assertions.assertNotEquals(itemDtoMustNotBeReturned, returnedItemDto);
    }

    @Test
    void getItemByItemIdAndUserIdIsOwnerTest() {
        User user = setUser();
        User owner = setOwner();
        Item item = setItem(owner);

        Comment comment = setComment(user, item);
        Booking lastBooking = setLastBooking(user, item);
        Booking nextBooking = setNextBooking(user, item);

        ItemDto itemDtoMustReturned = setItemDto(item.getId(), owner.getId());
        itemDtoMustReturned.setComments(List.of(CommentMapper.toCommentDto(comment)));
        itemDtoMustReturned.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
        itemDtoMustReturned.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));

        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mockBookingRepository
                .findFirstByItemIdAndStartLessThanEqualAndStatus(
                        anyLong(), any(LocalDateTime.class), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(Optional.of(lastBooking));
        when(mockBookingRepository
                .findFirstByItemIdAndStartAfterAndStatus(
                        anyLong(), any(LocalDateTime.class), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(Optional.of(nextBooking));
        when(mockCommentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));
        InOrder inOrder = inOrder(mockItemRepository);

        ItemDto returnedItemDto = itemServiceImpl.get(item.getId(), owner.getId());

        inOrder.verify(mockItemRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockItemRepository);
        Assertions.assertEquals(itemDtoMustReturned, returnedItemDto);
    }

    @Test
    void getAllItemsByOwnerTest() {
        User user = setUser();
        User owner = setOwner();
        Item item1 = setItem(owner);
        item1.setId(1L);
        Item item2 = setItem(owner);
        item2.setId(2L);

        Comment comment1 = setComment(user, item1);
        Booking lastBooking1 = setLastBooking(user, item1);
        Booking nextBooking1 = setNextBooking(user, item1);

        Comment comment2 = setComment(user, item2);
        Booking lastBooking2 = setLastBooking(user, item2);
        Booking nextBooking2 = setNextBooking(user, item2);

        ItemDto itemDto1 = setItemDto(item1.getId(), owner.getId());
        ItemDto itemDto2 = setItemDto(item2.getId(), owner.getId());

        itemDto1.setComments(List.of(CommentMapper.toCommentDto(comment1)));
        itemDto1.setLastBooking(BookingMapper.toBookingShortDto(lastBooking1));
        itemDto1.setNextBooking(BookingMapper.toBookingShortDto(nextBooking1));

        itemDto2.setComments(List.of(CommentMapper.toCommentDto(comment2)));
        itemDto2.setLastBooking(BookingMapper.toBookingShortDto(lastBooking2));
        itemDto2.setNextBooking(BookingMapper.toBookingShortDto(nextBooking2));

        List<ItemDto> itemDtoMustReturnedList = List.of(itemDto1, itemDto2);

        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRepository.findAllByOwnerIdOrderByIdAsc(anyLong())).thenReturn(List.of(item1, item2));

        when(mockBookingRepository
                .findByItemInAndStartLessThanEqualAndStatus(
                        anyList(), any(LocalDateTime.class), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(List.of(lastBooking1, lastBooking2));
        when(mockBookingRepository
                .findByItemInAndStartAfterAndStatus(
                        anyList(), any(LocalDateTime.class), any(BookingStatus.class), any(Sort.class)))
                .thenReturn(List.of(nextBooking1, nextBooking2));
        when(mockCommentRepository.findByItemIn(anyList(), any(Sort.class))).thenReturn(List.of(comment1, comment2));
        InOrder inOrder = inOrder(mockItemRepository);

        List<ItemDto> returnedItemDtoList = itemServiceImpl.getAllByOwnerId(owner.getId());

        inOrder.verify(mockItemRepository, times(1)).findAllByOwnerIdOrderByIdAsc(anyLong());
        verifyNoMoreInteractions(mockItemRepository);
        Assertions.assertEquals(itemDtoMustReturnedList, returnedItemDtoList);
    }

    @Test
    void searchItemByText() {
        User owner = setOwner();
        Item item = setItem(owner);
        String text = "TestItem";

        List<ItemDto> itemDtoMustReturnedList = List.of(setItemDto(item.getId(), owner.getId()));

        when(mockItemRepository.getByText(text)).thenReturn(List.of(item));
        InOrder inOrder = inOrder(mockItemRepository);

        List<ItemDto> returnedItemDtoList = itemServiceImpl.getByText(text);

        inOrder.verify(mockItemRepository, times(1)).getByText(anyString());
        verifyNoMoreInteractions(mockItemRepository);
        Assertions.assertEquals(itemDtoMustReturnedList, returnedItemDtoList);
    }

    @Test
    void updateItem() {
        String newName = "NewTestItem";
        String newDescription = "New test description";
        User owner = setOwner();
        Item oldItem = setItem(owner);
        Item newItem = setItem(owner);
        newItem.setName(newName);
        newItem.setDescription(newDescription);

        ItemUpdateDto itemUpdateDto = setItemUpdateDto(oldItem.getId(), owner.getId());

        ItemDto itemDtoMustReturned = ItemDto.builder()
                .id(oldItem.getId())
                .name(newName)
                .description(newDescription)
                .requestId(null)
                .available(true)
                .ownerId(owner.getId())
                .comments(new ArrayList<>())
                .lastBooking(null)
                .nextBooking(null)
                .build();

        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(oldItem));
        when(mockItemRepository.save(any(Item.class))).thenReturn(newItem);
        InOrder inOrder = inOrder(mockItemRepository);

        ItemDto returnedItemDto = itemServiceImpl.update(itemUpdateDto);

        inOrder.verify(mockItemRepository, times(1)).findById(anyLong());
        inOrder.verify(mockItemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(mockItemRepository);
        Assertions.assertEquals(itemDtoMustReturned, returnedItemDto);
    }

    private User setUser() {
        return User.builder()
                .id(1L)
                .name("TestUser")
                .email("TestUserEmail@test.com")
                .build();
    }

    private User setOwner() {
        return User.builder()
                .id(2L)
                .name("TestOwner")
                .email("TestOwnerEmail@test.com")
                .build();
    }

    private Item setItem(User owner) {
        return Item.builder()
                .id(1L)
                .name("TestItem")
                .description("Test description")
                .available(true)
                .owner(owner)
                .request(null)
                .build();
    }

    private ItemDto setItemDto(long itemId, long ownerId) {
        return ItemDto.builder()
                .id(itemId)
                .name("TestItem")
                .description("Test description")
                .requestId(null)
                .available(true)
                .ownerId(ownerId)
                .comments(new ArrayList<>())
                .lastBooking(null)
                .nextBooking(null)
                .build();
    }

    private ItemUpdateDto setItemUpdateDto(long itemId, long ownerId) {
        return ItemUpdateDto.builder()
                .id(itemId)
                .name("NewTestItem")
                .description("New test description")
                .ownerId(ownerId)
                .available(true)
                .build();
    }

    private ItemCreateDto setItemCreateDto() {
        return ItemCreateDto.builder()
                .name("TestItem")
                .description("Test description")
                .requestId(null)
                .available(true)
                .build();
    }

    private Comment setComment(User user, Item item) {
        return Comment.builder()
                .id(1L)
                .text("Test comment")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
    }

    private Booking setLastBooking(User user, Item item) {
        return Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(LocalDateTime.of(2024, 7, 27, 0, 0))
                .end(LocalDateTime.of(2024, 8, 1, 0, 0))
                .status(BookingStatus.CANCELLED)
                .build();
    }

    private Booking setNextBooking(User user, Item item) {
        return Booking.builder()
                .id(2L)
                .booker(user)
                .item(item)
                .start(LocalDateTime.of(2024, 8, 20, 0, 0))
                .end(LocalDateTime.of(2024, 8, 21, 0, 0))
                .status(BookingStatus.CANCELLED)
                .build();
    }
}