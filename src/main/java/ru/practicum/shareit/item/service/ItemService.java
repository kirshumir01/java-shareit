package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto getItemById(long itemId);

    List<ItemDto> getAllByOwnerId(long userId);

    List<ItemDto> getByText(String text);

    ItemDto update(ItemDto newItemDto);
}