package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, ItemRequestCreateDto itemRequestCreateDto);

    List<ItemRequestDtoWithAnswers> getAllUserRequests(long userId);

    List<ItemRequestDto> getAllRequestsExceptUser(long userId);

    ItemRequestDtoWithAnswers getRequestById(long requestId);
}