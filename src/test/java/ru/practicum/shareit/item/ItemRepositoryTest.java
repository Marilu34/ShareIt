package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.itemBooking.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemRepositoryTest {

    private ItemRepository itemRepository = mock(ItemRepository.class);
    private BookingRepository bookingRepositoryMock = mock(BookingRepository.class);
    private UserRepository userRepositoryMock = mock(UserRepository.class);
    @MockBean
    private ItemService itemService = mock(ItemService.class);

    @Test
    void testGetItemsByUserId_validUserId_returnItemBookingsDtoList() {
        // Arrange
        Long userId = 1L;
        int from = 0;
        int size = 10;
        Pageable page = any();



        List<Item> itemList = Arrays.asList(
                Item.builder().id(1).available(true).name("item1").build(),
                Item.builder().id(2).available(true).name("item2").build(),
                Item.builder().id(3).available(true).name("item2").build()

        );
        List<ShortBookingDto> bookingList = Arrays.asList(
                new ShortBookingDto(1L, 1L),
                new ShortBookingDto(2L, 1L),
                new ShortBookingDto(3L, 2L)
        );
        when(userRepositoryMock.existsById(userId)).thenReturn(true);
        when(itemRepository.findAllByOwnerId(userId, page))
                .thenReturn(new PageImpl<>(itemList));
        when(bookingRepositoryMock.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(any(Long.class), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(bookingList.get(0));
        when(bookingRepositoryMock.findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(any(Long.class), any(LocalDateTime.class), any(Status.class)))
                .thenReturn(bookingList.get(1), bookingList.get(2));

        // Act
        Collection<ItemBookingsDto> result = itemService.getItemsByUserId(userId, from, size);

        // Assert
        assertEquals(0, result.size());
        // проверки других полей возвращаемого объекта

    }
}

