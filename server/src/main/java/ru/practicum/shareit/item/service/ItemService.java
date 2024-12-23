package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemCreateDto itemCreateDto, long userId);

    ItemDto get(long itemId, long userId);

    List<ItemDto> getAllByOwnerId(long userId);

    List<ItemDto> getByText(String text);

    ItemDto update(ItemUpdateDto itemUpdateDto);
}