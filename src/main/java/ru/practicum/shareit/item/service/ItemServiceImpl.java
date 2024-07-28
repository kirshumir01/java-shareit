package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        Item createdItem = itemMapper.toItem(itemDto);
        User itemOwner = userRepository.get(userId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", userId));
        });
        createdItem.setOwner(itemOwner);
        return itemMapper.toItemDto(itemRepository.create(createdItem));
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
        checkUserExistent(userId);
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
    public ItemDto update(ItemDto newItemDto) {
        checkUserExistent(newItemDto.getOwnerId());

        Item item = itemRepository.getItemById(newItemDto.getId()).orElseThrow(() -> {
            throw new NotFoundException(String.format("Вещь с идентификатором %d не найдена", newItemDto.getId()));
        });
        Item itemForUpdate = itemMapper.toItem(newItemDto);

        if (!item.getOwner().getId().equals(newItemDto.getOwnerId())) {
            throw new NotOwnerException(String.format("Пользователь с идентификатором %d" +
                    " не является собственником вещи '%s'", item.getOwner().getId(), item.getName()));
        } else {
            item.setName(Objects.requireNonNullElse(itemForUpdate.getName(), item.getName()));
            item.setDescription(Objects.requireNonNullElse(itemForUpdate.getDescription(), item.getDescription()));
            item.setAvailable(Objects.requireNonNullElse(itemForUpdate.getAvailable(), item.getAvailable()));
        }
        return itemMapper.toItemDto(itemRepository.update(item));
    }

    private void checkUserExistent(long userId) {
        if (userRepository.get(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", userId));
        }
    }
}