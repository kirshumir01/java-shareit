package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDataDto;
import ru.practicum.shareit.item.comment.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public static ItemDto toItemDto(
            Item item,
            BookingDataDto lastBooking,
            BookingDataDto nextBooking,
            List<CommentOutputDto> commentOutputDtoList) {

        return ItemDto
                .builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentOutputDtoList)
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
    }

    public static ItemDto toItemDto(Item item, List<CommentOutputDto> commentOutputDtoList) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .lastBooking(null)
                .nextBooking(null)
                .comments(commentOutputDtoList)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item
                .builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}