package ru.practicum.shareit.item.comment.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    CommentRepository mockCommentRepository;

    @Mock
    UserRepository mockUserRepository;

    @Mock
    ItemRepository mockItemRepository;

    @Mock
    BookingRepository mockBookingRepository;

    @InjectMocks
    CommentServiceImpl commentServiceImpl;

    @Test
    void createCommentTest() {
        long itemId = 1L;
        BookingStatus status = BookingStatus.APPROVED;
        LocalDateTime created = LocalDateTime.of(2024, 8, 9, 23, 11, 29);

        User author = new User(1L, "Author", "TestUSerEmail@test.com");
        Item item = new Item(1L, "TetsItem", "Test description", true, null, null);

        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, author, status);
        CommentCreateDto commentCreateDto = new CommentCreateDto("Test comment");

        Comment comment = new Comment(1L, "Test comment", item, author, created);
        CommentDto commentDtoMustReturned = new CommentDto(1L, "Test comment", "Author", created);

        when(mockBookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(mockItemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(comment);
        InOrder inOrder = Mockito.inOrder(mockCommentRepository);

        CommentDto returnedCommentDto = commentServiceImpl.createComment(commentCreateDto, itemId, author.getId());

        inOrder.verify(mockCommentRepository, times(1)).save(any(Comment.class));
        verifyNoMoreInteractions(mockCommentRepository);
        Assertions.assertEquals(commentDtoMustReturned, returnedCommentDto);
    }
}