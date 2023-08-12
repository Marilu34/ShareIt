package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemBooking.ItemCommentsDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapperTest {


    @Test
    public void toItemDto_ReturnsCorrectItemDto() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setName("Test Item");
        item.setDescription("Item description");

        ItemRequest request = new ItemRequest();
        request.setId(2L);
        item.setRequest(request);

        // Act
        ItemDto itemDto = ItemMapper.toItemDto(item);

        // Assert
        Assertions.assertEquals(item.getId(), itemDto.getId());
        Assertions.assertTrue(itemDto.getAvailable());
        Assertions.assertEquals(item.getName(), itemDto.getName());
        Assertions.assertEquals(item.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(request.getId(), itemDto.getRequestId());
    }

    @Test
    public void fromItemDto_ReturnsCorrectItem() {
        // Arrange
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .available(true)
                .name("Test Item")
                .description("Item description")
                .requestId(2L)
                .build();

        User owner = new User();
        owner.setName("John");

        ItemRequest request = new ItemRequest();
        request.setId(2L);

        // Act
        Item item = ItemMapper.fromItemDto(itemDto, owner, request);

        // Assert
        Assertions.assertEquals(itemDto.getId(), item.getId());
        Assertions.assertTrue(item.isAvailable());
        Assertions.assertEquals(itemDto.getName(), item.getName());
        Assertions.assertEquals(itemDto.getDescription(), item.getDescription());
        Assertions.assertEquals(owner, item.getOwner());
        Assertions.assertEquals(request, item.getRequest());
    }

    @Test
    public void toItemBookingsDto_ReturnsCorrectItemBookingsDto() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setName("Test Item");
        item.setDescription("Item description");

        ShortBookingDto lastBooking = new ShortBookingDto(1L, 123L);
        lastBooking.setId(100L);

        ShortBookingDto nextBooking = new ShortBookingDto(1L, 123L);
        nextBooking.setId(200L);

        // Act
        ItemBookingsDto itemBookingsDto = ItemMapper.toItemBookingsDto(item, lastBooking, nextBooking);

        // Assert
        Assertions.assertEquals(item.getId(), itemBookingsDto.getId());
        Assertions.assertTrue(itemBookingsDto.getAvailable());
        Assertions.assertEquals(item.getName(), itemBookingsDto.getName());
        Assertions.assertEquals(item.getDescription(), itemBookingsDto.getDescription());
        Assertions.assertEquals(lastBooking, itemBookingsDto.getLastBooking());
        Assertions.assertEquals(nextBooking, itemBookingsDto.getNextBooking());
        Assertions.assertNull(itemBookingsDto.getRequestId());
    }

    @Test
    public void toItemCommentDto_ReturnsCorrectItemCommentsDto() {
        // Arrange
        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setName("Test Item");
        item.setDescription("Item description");

        ShortBookingDto lastBooking = new ShortBookingDto(1L, 123L);
        lastBooking.setId(100L);

        ShortBookingDto nextBooking = new ShortBookingDto(1L, 123L);
        nextBooking.setId(200L);

        List<CommentDto> comments = new ArrayList<>();
        CommentDto comment1 = new CommentDto();
        comment1.setId(300L);
        CommentDto comment2 = new CommentDto();
        comment2.setId(400L);
        comments.add(comment1);
        comments.add(comment2);

        // Act
        ItemCommentsDto itemCommentsDto = ItemMapper.toItemCommentDto(item, lastBooking, nextBooking, comments);

        // Assert
        Assertions.assertEquals(item.getId(), itemCommentsDto.getId());
        Assertions.assertTrue(itemCommentsDto.getAvailable());
        Assertions.assertEquals(item.getName(), itemCommentsDto.getName());
        Assertions.assertEquals(item.getDescription(), itemCommentsDto.getDescription());
        Assertions.assertEquals(lastBooking, itemCommentsDto.getLastBooking());
        Assertions.assertEquals(nextBooking, itemCommentsDto.getNextBooking());
        Assertions.assertEquals(comments, itemCommentsDto.getComments());
        Assertions.assertNull(itemCommentsDto.getRequestId());
    }
}
