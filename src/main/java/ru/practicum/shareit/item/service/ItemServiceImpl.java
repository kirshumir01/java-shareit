package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDataDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.comment.dao.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentInputDto;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, long userId) {
        Item item = ItemMapper.toItem(itemDto);
        User itemOwner = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id %d не найден.", userId)));
        item.setOwner(itemOwner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto get(long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Вещь с идентификатором %d не найдена", itemId)));
        return ItemMapper.toItemDto(item, getAllCommentsByItemId(itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllByOwnerId(long userId) {
        checkUserExists(userId);
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(item -> ItemMapper.toItemDto(
                        item,
                        getLastBooking(item),
                        getNextBooking(item),
                        getAllCommentsByItemId(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getByText(String text) {
        if (text.isBlank() || text.isEmpty()) return List.of();
        return itemRepository.getByText(text)
                .stream()
                .map(item -> ItemMapper.toItemDto(
                        item,
                        getLastBooking(item),
                        getNextBooking(item),
                        getAllCommentsByItemId(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto newItemDto) {
        checkUserExists(newItemDto.getOwnerId());
        checkItemExists(newItemDto.getId());

        Item item = itemRepository.findById(newItemDto.getId()).orElseThrow(
                () -> new NotFoundException(String.format("Вещь с идентификатором %d не найдена", newItemDto.getId())));
        Item itemForUpdate = ItemMapper.toItem(newItemDto);

        if (!item.getOwner().getId().equals(newItemDto.getOwnerId())) {
            throw new NotOwnerException(String.format("Пользователь с идентификатором %d" +
                    " не является собственником вещи '%s'", item.getOwner().getId(), item.getName()));
        } else {
            item.setName(Objects.requireNonNullElse(itemForUpdate.getName(), item.getName()));
            item.setDescription(Objects.requireNonNullElse(itemForUpdate.getDescription(), item.getDescription()));
            item.setAvailable(Objects.requireNonNullElse(itemForUpdate.getAvailable(), item.getAvailable()));
        }
        return ItemMapper.toItemDto(
                itemRepository.save(item),
                getLastBooking(item),
                getNextBooking(item),
                getAllCommentsByItemId(item.getId()));
    }

    @Override
    @Transactional
    public CommentOutputDto createComment(CommentInputDto commentInputDto, long userId, long itemId) {
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, Status.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException(String.format("Пользователь с id = {} не бронировал вещь c id = {}", userId, itemId));
        }

        User author = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id %d не найден.", userId)));

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Вещь с идентификатором %d не найдена", itemId)));

        return CommentMapper.toCommentOutputDto(commentRepository.save(
                Comment.builder()
                        .text(commentInputDto.getText())
                        .created(LocalDateTime.now())
                        .author(author)
                        .item(item)
                        .build()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentOutputDto> getAllCommentsByItemId(long itemId) {
        checkItemExists(itemId);
        return CommentMapper.toCommentOutputDtos(commentRepository.findAllByItemId(itemId));
    }

    private BookingDataDto getLastBooking(Item item) {
        long itemId = item.getId();
        return bookingRepository
                .getLastBooking(itemId, LocalDateTime.now())
                .map(BookingMapper::toBookingDataDto)
                .orElse(null);
    }

    private BookingDataDto getNextBooking(Item item) {
        return bookingRepository
                .getNextBooking(item.getId(), LocalDateTime.now())
                .map(BookingMapper::toBookingDataDto)
                .orElse(null);
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id %d не найден.", userId));
        }
    }

    private void checkItemExists(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(String.format("Вещь с идентификатором %d не найдена", itemId));
        }
    }
}