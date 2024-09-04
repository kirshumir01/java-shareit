package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto create(ItemCreateDto itemCreateDto, long userId) {
        User itemOwner = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User not found by id = %d", userId)));
        Long requestId = itemCreateDto.getRequestId();
        Item itemForCreate = ItemMapper.toItem(itemCreateDto);
        itemForCreate.setOwner(itemOwner);
        if (requestId != null) {
            itemForCreate.setRequest(itemRequestRepository.findById(requestId).orElseThrow(() ->
                    new NotFoundException(String.format("Item request by id = %d not found", requestId))));
        }
        return ItemMapper.toItemDto(itemRepository.save(itemForCreate));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto get(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException(String.format("Item not found by id = %d", itemId)));

        ItemDto itemDto = addCommentsAndBookingsToItem(item);

        if (!item.getOwner().getId().equals(userId)) {
            itemDto.setNextBooking(null);
            itemDto.setLastBooking(null);
        }
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAllByOwnerId(long ownerId) {
        checkUserExists(ownerId);
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId);
        return addCommentsAndBookingsToItems(items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getByText(String text) {
        if (text.isBlank() || text.isEmpty()) return List.of();
        return itemRepository.getByText(text).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    @Transactional
    public ItemDto update(ItemUpdateDto itemUpdateDto) {
        checkUserExists(itemUpdateDto.getOwnerId());
        checkItemExists(itemUpdateDto.getId());

        Item item = itemRepository.findById(itemUpdateDto.getId()).orElseThrow(
                () -> new NotFoundException(String.format("Item not found by id = %d", itemUpdateDto.getId())));

        if (!item.getOwner().getId().equals(itemUpdateDto.getOwnerId())) {
            throw new NotOwnerException(String.format("User with id = %d" +
                    " are not item's '%s' owner", item.getOwner().getId(), item.getName()));
        } else {
            item.setName(Objects.requireNonNullElse(itemUpdateDto.getName(), item.getName()));
            item.setDescription(Objects.requireNonNullElse(itemUpdateDto.getDescription(), item.getDescription()));
            item.setAvailable(Objects.requireNonNullElse(itemUpdateDto.getAvailable(), item.getAvailable()));
        }
        Item updatedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(updatedItem);
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User not found by id = %d", userId));
        }
    }

    private void checkItemExists(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(String.format("Item not found by id = %d", itemId));
        }
    }

    private ItemDto addCommentsAndBookingsToItem(Item item) {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        itemDto.setLastBooking(bookingRepository
                .findFirstByItemIdAndStartLessThanEqualAndStatus(
                        itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"))
                .map(BookingMapper::toBookingShortDto)
                .orElse(null));

        itemDto.setNextBooking(bookingRepository
                .findFirstByItemIdAndStartAfterAndStatus(
                        itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"))
                .map(BookingMapper::toBookingShortDto)
                .orElse(null));

        itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .toList());

        return itemDto;
    }

    private List<ItemDto> addCommentsAndBookingsToItems(List<Item> items) {
        List<ItemDto> itemDtoList = new ArrayList<>();

        Map<Item, Booking> itemsWithLastBookings = bookingRepository
                .findByItemInAndStartLessThanEqualAndStatus(
                        items, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "end"))
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (o1, o2) -> o1));

        Map<Item, Booking> itemsWithNextBookings = bookingRepository
                .findByItemInAndStartAfterAndStatus(
                        items, LocalDateTime.now(), BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "end"))
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (o1, o2) -> o1));

        Map<Item, List<Comment>> itemsWithComments = commentRepository.findByItemIn(
                        items, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        for (Item item : items) {
            ItemDto itemDto = ItemMapper.toItemDto(item);

            Booking lastBooking = itemsWithLastBookings.get(item);
            if (!itemsWithLastBookings.isEmpty() && lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
            }

            Booking nextBooking = itemsWithNextBookings.get(item);
            if (!itemsWithNextBookings.isEmpty() && nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
            }

            List<CommentDto> commentDtos = itemsWithComments.getOrDefault(item, Collections.emptyList())
                    .stream()
                    .map(CommentMapper::toCommentDto)
                    .toList();
            itemDto.setComments(commentDtos);

            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }
}