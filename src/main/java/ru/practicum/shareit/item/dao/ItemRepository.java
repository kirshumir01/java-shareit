package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item create(Item item, long userId);

    Optional<Item> getItemById(long itemId);

    List<Item> getAllByOwnerId(long userId);

    List<Item> getByText(String text);

    Item update(Item newItem, long itemId);
}