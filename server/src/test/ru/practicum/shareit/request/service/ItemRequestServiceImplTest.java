package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository mockItemRequestRepository;

    @Mock
    UserRepository mockUserRepository;

    @Mock
    ItemRepository mockItemRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestServiceImpl;

    @Test
    void createItemRequestTest() {
        long userId = 1L;

        User requestor = setRequestor(userId);
        ItemRequest itemRequest = setItemRequest(requestor);
        ItemRequestCreateDto itemRequestCreateDto = setItemRequestCreateDto();
        ItemRequestDto itemRequestDtoMustReturned = setItemRequestDto();

        when(mockUserRepository.findById(anyLong())).thenReturn(Optional.ofNullable(requestor));
        when(mockItemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        InOrder inOrder = inOrder(mockItemRequestRepository);

        ItemRequestDto returnedItemRequestDto = itemRequestServiceImpl.create(userId, itemRequestCreateDto);

        inOrder.verify(mockItemRequestRepository, times(1)).save(any(ItemRequest.class));
        verifyNoMoreInteractions(mockItemRequestRepository);
        Assertions.assertEquals(itemRequestDtoMustReturned, returnedItemRequestDto);
    }

    @Test
    void getAllUserRequestsTest() {
        long userId = 1L;

        User requestor = setRequestor(userId);

        List<ItemRequest> itemRequestsList = setItemRequestList(requestor);
        List<Item> items = setItemsList(requestor);

        items.get(0).setRequest(itemRequestsList.get(0));
        items.get(1).setRequest(itemRequestsList.get(1));
        items.get(2).setRequest(itemRequestsList.get(2));

        ItemRequestDtoWithAnswers itemRequestDtoWithAnswers1 =
                new ItemRequestDtoWithAnswers(1L, "Test item request 1", LocalDateTime.of(2024, 9, 1, 0, 23, 34), null);
        ItemRequestDtoWithAnswers itemRequestDtoWithAnswers2 =
                new ItemRequestDtoWithAnswers(2L, "Test item request 2", LocalDateTime.of(2024, 9, 1, 0, 24, 34), null);
        ItemRequestDtoWithAnswers itemRequestDtoWithAnswers3 =
                new ItemRequestDtoWithAnswers(3L, "Test item request 3", LocalDateTime.of(2024, 9, 1, 0, 25, 34), null);

        ItemShortDto itemShortDto1 = new ItemShortDto(1L, "Test item 1", requestor.getId());
        ItemShortDto itemShortDto2 = new ItemShortDto(2L, "Test item 2", requestor.getId());
        ItemShortDto itemShortDto3 = new ItemShortDto(3L, "Test item 3", requestor.getId());

        itemRequestDtoWithAnswers1.setItems(List.of(itemShortDto1));
        itemRequestDtoWithAnswers2.setItems(List.of(itemShortDto2));
        itemRequestDtoWithAnswers3.setItems(List.of(itemShortDto3));

        List<ItemRequestDtoWithAnswers> itemRequestDtoListMustReturned =
                List.of(itemRequestDtoWithAnswers1, itemRequestDtoWithAnswers2, itemRequestDtoWithAnswers3);

        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRequestRepository.findAllByRequestor(anyLong())).thenReturn(itemRequestsList);
        when(mockItemRepository.findByRequestId(anyList())).thenReturn(items);
        InOrder inOrder = inOrder(mockItemRequestRepository);

        List<ItemRequestDtoWithAnswers> returnedItemRequestDtoList = itemRequestServiceImpl.getAllUserRequests(userId);

        inOrder.verify(mockItemRequestRepository, times(1)).findAllByRequestor(anyLong());
        verifyNoMoreInteractions(mockItemRequestRepository);
        Assertions.assertEquals(itemRequestDtoListMustReturned, returnedItemRequestDtoList);
    }

    @Test
    void getAllRequestExceptUserTest() {
        long userId = 1L;
        long otherRequestorId = 2L;

        User otherRequestor = setRequestor(otherRequestorId);

        List<ItemRequest> itemRequestsList = setItemRequestList(otherRequestor);
        List<ItemRequestDto> itemRequestDtoListMustReturned = setItemRequestDtoList();

        when(mockUserRepository.existsById(anyLong())).thenReturn(true);
        when(mockItemRequestRepository.findAllExceptRequestor(anyLong())).thenReturn(itemRequestsList);
        InOrder inOrder = inOrder(mockItemRequestRepository);

        List<ItemRequestDto> returnedItemRequestDtoList = itemRequestServiceImpl.getAllRequestsExceptUser(userId);

        inOrder.verify(mockItemRequestRepository, times(1)).findAllExceptRequestor(anyLong());
        verifyNoMoreInteractions(mockItemRequestRepository);
        Assertions.assertEquals(itemRequestDtoListMustReturned, returnedItemRequestDtoList);
    }

    @Test
    void getRequestByIdTest() {
        long userId = 1L;
        long itemRequestId = 1L;

        User requestor = setRequestor(userId);

        ItemRequest itemRequest = new ItemRequest(1L, "Test item request 1", requestor, LocalDateTime.of(2024, 9, 1, 0, 23, 34));

        Item item = new Item(1L, "Test item 1", "Test description", true, requestor, null);
        item.setRequest(itemRequest);

        ItemRequestDtoWithAnswers itemRequestDtoWithAnswersMustReturned =
                new ItemRequestDtoWithAnswers(1L, "Test item request 1", LocalDateTime.of(2024, 9, 1, 0, 23, 34), null);

        ItemShortDto itemShortDto = new ItemShortDto(1L, "Test item 1", requestor.getId());
        itemRequestDtoWithAnswersMustReturned.setItems(List.of(itemShortDto));

        when(mockItemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(mockItemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        InOrder inOrder = inOrder(mockItemRequestRepository);

        ItemRequestDtoWithAnswers returnedItemRequestDto = itemRequestServiceImpl.getRequestById(itemRequestId);

        inOrder.verify(mockItemRequestRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(mockItemRequestRepository);
        Assertions.assertEquals(itemRequestDtoWithAnswersMustReturned, returnedItemRequestDto);
    }

    private User setRequestor(long requestorId) {
        return User.builder()
                .id(requestorId)
                .name("TestRequestor")
                .email("TestRequestorEmail@test.com")
                .build();
    }

    private ItemRequestDto setItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .description("Test item request description")
                .created(LocalDateTime.of(2024, 8, 29, 14, 59, 34))
                .build();
    }

    private ItemRequestCreateDto setItemRequestCreateDto() {
        return ItemRequestCreateDto.builder()
                .description("Test item request description")
                .build();
    }

    private List<ItemShortDto> setItemShortDtoList() {
        ItemShortDto itemShortDto1 = new ItemShortDto(1L, "Test name", 231L);
        ItemShortDto itemShortDto2 = new ItemShortDto(2L, "Test name", 224L);
        ItemShortDto itemShortDto3 = new ItemShortDto(3L, "Test name", 223L);
        return List.of(itemShortDto1, itemShortDto2, itemShortDto3);
    }

    private ItemRequest setItemRequest(User requestor) {
        return ItemRequest.builder()
                .id(1L)
                .description("Test item request description")
                .created(LocalDateTime.of(2024, 8, 29, 14, 59, 34))
                .requestor(requestor)
                .build();
    }

    private List<ItemRequest> setItemRequestList(User requestor) {
        ItemRequest itemRequest1 = new ItemRequest(1L, "Test item request 1", requestor, LocalDateTime.of(2024, 9, 1, 0, 23, 34));
        ItemRequest itemRequest2 = new ItemRequest(2L, "Test item request 2", requestor, LocalDateTime.of(2024, 9, 1, 0, 24, 34));
        ItemRequest itemRequest3 = new ItemRequest(3L, "Test item request 3", requestor, LocalDateTime.of(2024, 9, 1, 0, 25, 34));

        return List.of(itemRequest1, itemRequest2, itemRequest3);
    }

    private List<Item> setItemsList(User requestor) {
        Item item1 = new Item(1L, "Test item 1", "Test description", true, requestor, null);
        Item item2 = new Item(2L, "Test item 2", "Test description", true, requestor, null);
        Item item3 = new Item(3L, "Test item 3", "Test description", true, requestor, null);

        return List.of(item1, item2, item3);
    }

    private List<ItemRequestDto> setItemRequestDtoList() {
        ItemRequestDto itemRequestDto1 = new ItemRequestDto(1L, "Test item request 1", LocalDateTime.of(2024, 9, 1, 0, 23, 34));
        ItemRequestDto itemRequestDto2 = new ItemRequestDto(2L, "Test item request 2", LocalDateTime.of(2024, 9, 1, 0, 24, 34));
        ItemRequestDto itemRequestDto3 = new ItemRequestDto(3L, "Test item request 3", LocalDateTime.of(2024, 9, 1, 0, 25, 34));

        return List.of(itemRequestDto1, itemRequestDto2, itemRequestDto3);
    }
}