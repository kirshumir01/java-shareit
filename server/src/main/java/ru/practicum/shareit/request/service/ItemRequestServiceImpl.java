package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public ItemRequestDto create(long userId, ItemRequestCreateDto itemRequestCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User not found by id = %d", userId)));
        ItemRequest requestForCreate = ItemRequestMapper.toItemRequest(itemRequestCreateDto, user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(requestForCreate));
    }

    @Override
    @Transactional
    public List<ItemRequestDtoWithAnswers> getAllUserRequests(long userId) {
        checkUserExists(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestor(userId);
        return addAnswersToItemRequests(requests);
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getAllRequestsExceptUser(long userId) {
        checkUserExists(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllExceptRequestor(userId);
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemRequestDtoWithAnswers getRequestById(long requestId) {
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException(String.format("Item request with id = %d not found", requestId)));
        return addAnswersToItemRequest(request);
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User not found by id = %d", userId));
        }
    }

    private List<ItemRequestDtoWithAnswers> addAnswersToItemRequests(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();
        List<Item> items = itemRepository.findByRequestId(requestIds);
        Map<Long, List<ItemShortDto>> map = new HashMap<>();
        for (Item item : items) {
            if (!map.containsKey(item.getRequest().getId())) {
                map.put(item.getId(), new ArrayList<>());
            }
            List<ItemShortDto> itemShortDtos = map.get(item.getId());
            itemShortDtos.add(ItemMapper.toItemShortDto(item));
            map.put(item.getId(), itemShortDtos);
        }
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestShortDtoWithAnswers)
                .peek(itemRequestDtoWithAnswers ->
                        itemRequestDtoWithAnswers.setItems(map.get(itemRequestDtoWithAnswers.getId())))
                .toList();
    }

    private ItemRequestDtoWithAnswers addAnswersToItemRequest(ItemRequest request) {
        ItemRequestDtoWithAnswers itemRequestDtoWithAnswers = ItemRequestMapper.toItemRequestShortDtoWithAnswers(request);
        List<ItemShortDto> items = itemRepository.findByRequestId(request.getId())
                .stream()
                .map(ItemMapper::toItemShortDto)
                .toList();
        itemRequestDtoWithAnswers.setItems(items);
        return itemRequestDtoWithAnswers;
    }
}