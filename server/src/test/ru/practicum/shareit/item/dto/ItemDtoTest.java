package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemDtoTest {
    private final JacksonTester<ItemDto> json;

    private CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("Test comment")
            .authorName("Test author")
            .created(LocalDateTime.now())
            .build();

    private BookingDto.BookingShortDto lastBookingShortDto = BookingDto.BookingShortDto.builder()
            .id(1L)
            .bookerId(70L)
            .start(LocalDateTime.of(2024, 7, 27, 0, 0))
            .end(LocalDateTime.of(2024, 8, 1, 0, 0))
            .status(BookingStatus.CANCELLED)
            .build();

    private BookingDto.BookingShortDto nextBookingShortDto = BookingDto.BookingShortDto.builder()
            .id(2L)
            .bookerId(99L)
            .start(LocalDateTime.of(2024, 8, 20, 0, 0))
            .end(LocalDateTime.of(2024, 8, 21, 0, 0))
            .status(BookingStatus.CANCELLED)
            .build();

    @Test
    void serializeItemDtoTest() throws Exception {

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("TestItem")
                .description("Test description")
                .requestId(1L)
                .available(true)
                .ownerId(1L)
                .comments(List.of(commentDto))
                .lastBooking(lastBookingShortDto)
                .nextBooking(nextBookingShortDto)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .satisfies(id -> assertThat(id.longValue()).isEqualTo(itemDto.getId()));

        assertThat(result).extractingJsonPathValue("$.name")
                .satisfies(name -> assertThat(name).isEqualTo(itemDto.getName()));

        assertThat(result).extractingJsonPathValue("$.description")
                .satisfies(description -> assertThat(description).isEqualTo(itemDto.getDescription()));

        assertThat(result).extractingJsonPathValue("$.comments[0].text")
                .satisfies(comment -> assertThat(comment).isEqualTo(itemDto.getComments().getFirst().getText()));

        assertThat(result).extractingJsonPathValue("$.lastBooking.start")
                .satisfies(start -> assertThat(start).isEqualTo(itemDto.getLastBooking().getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        assertThat(result).extractingJsonPathValue("$.nextBooking.start")
                .satisfies(start -> assertThat(start).isEqualTo(itemDto.getNextBooking().getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }

    @Test
    void getterAndSetterTest() {
        String name = "TestItem";
        String description = "Test description";

        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setComments(List.of(commentDto));
        itemDto.setLastBooking(lastBookingShortDto);
        itemDto.setNextBooking(nextBookingShortDto);

        assertEquals(name, itemDto.getName());
        assertEquals(description, itemDto.getDescription());
        assertEquals(commentDto, itemDto.getComments().getFirst());
        assertEquals(lastBookingShortDto, itemDto.getLastBooking());
        assertEquals(nextBookingShortDto, itemDto.getNextBooking());
    }

    @Test
    void equalsAndHashCodeTest() {
        String name = "TestItem";
        String description = "Test description";

        ItemDto itemDto1 = new ItemDto(1L, name, description, true, 1L,
                lastBookingShortDto, nextBookingShortDto, List.of(commentDto), 1L);
        ItemDto itemDto2 = new ItemDto(1L, name, description, true, 1L,
                lastBookingShortDto, nextBookingShortDto, List.of(commentDto), 1L);
        ItemDto itemDto3 = new ItemDto(2L, name, description, true, 1L,
                lastBookingShortDto, nextBookingShortDto, List.of(commentDto), 1L);
        ItemDto itemDto4 = new ItemDto(3L, name, description, true, 1L,
                lastBookingShortDto, nextBookingShortDto, List.of(commentDto), 1L);

        assertEquals(itemDto1, itemDto2);
        assertNotEquals(itemDto1, itemDto3);
        assertNotEquals(itemDto1, itemDto4);
    }
}