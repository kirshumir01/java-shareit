package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> itemsByUserId = new LinkedHashMap<>();
    private long currentId = 0L;

    @Override
    public Item create(Item item) {
        item.setId(++currentId);
        items.put(item.getId(), item);
        final List<Item> itemsByOwner = itemsByUserId.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        itemsByOwner.add(item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getAllByOwnerId(long userId) {
        return itemsByUserId.get(userId);
    }

    @Override
    public List<Item> getByText(String text) {
        return items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        (item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Item newItem) {
        items.put(newItem.getId(), newItem);
        List<Item> itemsByOwner = itemsByUserId.get(newItem.getOwner().getId());
        Item itemToRemove = items.get(newItem.getId());
        itemsByOwner.remove(itemToRemove);
        itemsByOwner.add(newItem);
        return newItem;
    }
}