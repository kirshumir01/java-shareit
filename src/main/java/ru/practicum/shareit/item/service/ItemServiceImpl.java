package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        userService.get(userId);
        return itemMapper.toItemDto(itemRepository.create(itemMapper.toItem(itemDto), userId));
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.getItemById(itemId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Вещь с идентификатором %d не найдена", itemId));
        });
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwnerId(long userId) {
        userService.get(userId);
        return itemRepository.getAllByOwnerId(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.getByText(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(long userId, ItemDto newItemDto, long itemId) {
        userService.get(userId);
        checkItemId(itemId);

        Item existentItem = itemRepository.getItemById(itemId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Вещь с идентификатором %d не найдена", itemId));
        });
        Item itemForUpdate = itemMapper.toItem(newItemDto);

        if (existentItem.getOwnerId() == userId) {
            existentItem.setName(Objects.requireNonNullElse(itemForUpdate.getName(), existentItem.getName()));
            existentItem.setDescription(Objects.requireNonNullElse(itemForUpdate.getDescription(), existentItem.getDescription()));
            existentItem.setAvailable(Objects.requireNonNullElse(itemForUpdate.getAvailable(), existentItem.getAvailable()));
        } else {
            throw new NotOwnerException(String.format("Пользователь с идентификатором %d" +
                    " не является собственником вещи '%s'", userId, existentItem.getName()));
        }
        return itemMapper.toItemDto(itemRepository.update(existentItem, itemId));
    }

    private void checkItemId(long id) {
        if (itemRepository.getItemById(id).isEmpty()) {
            throw new NotFoundException("Вещь с идентификатором " + id + " не найдена.");
        }
    }
}