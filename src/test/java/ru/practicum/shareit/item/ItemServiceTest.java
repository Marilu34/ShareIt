package ru.practicum.shareit.item;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.itemBooking.ItemCommentsDto;
import ru.practicum.shareit.item.itemBooking.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@TestPropertySource(properties = {"db.name=test"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    private static List<ItemDto> list;

    @Test
    void testCreateItem() {
        ItemDto item = itemService.createItem(1L, list.get(1));
        ItemDto item1 = list.get(1).toBuilder().id(item.getId()).build();

        assertNotNull(itemService.createItem(1L, item));
        assertNotNull(itemService.createItem(1L, item1));
    }

    @Test
    void testUpdate() {
        ItemDto item = itemService.createItem(2L, list.get(2));
        ItemDto item1 = item.toBuilder().name("NewName").build();

        assertEquals(item1, itemService.updateItem(2L, item1));

        ItemDto item2 = ItemDto.builder().id(item.getId()).available(false).build();

        assertEquals(item1.toBuilder().available(false).build(), itemService.updateItem(2L, item2));
        assertThrows(RuntimeException.class, () -> itemService.updateItem(123L, item));
    }


    @Test
    void testGetByItemId() {
        ItemDto expected = itemService.createItem(3L, list.get(3));

        assertEquals(expected, itemService.getByItemId(expected.getId()));
    }

    @Test
    void testGetByItemIdWithBookings() throws NullPointerException, InterruptedException {
        ItemDto itemDto = itemService.createItem(1L, list.get(1));
        long itemId = itemDto.getId();
        long ownerId = 1L;

        assertEquals(itemId, itemService.getByItemId(itemDto.getId(), ownerId).getId());
        assertNull(itemService.getByItemId(itemDto.getId(), ownerId).getLastBooking());
        assertNull(itemService.getByItemId(itemDto.getId(), ownerId).getNextBooking());

        long bookerId = 3L;
        long nextBookingId = bookingService.createBooking(
                new CreationBooking(itemDto.getId(),
                        LocalDateTime.now().plusSeconds(2),
                        LocalDateTime.now().plusSeconds(3),
                        bookerId)
        ).getId();
        long lastBookingId = bookingService.createBooking(
                new CreationBooking(itemDto.getId(),
                        LocalDateTime.now().plusNanos(100000000),
                        LocalDateTime.now().plusSeconds(1).plusNanos(500000000),
                        bookerId)
        ).getId();
        Thread.sleep(1000);

        ItemCommentsDto itemBeforeAcceptOfBookings = itemService.getByItemId(itemDto.getId(), ownerId);
        MatcherAssert.assertThat(itemBeforeAcceptOfBookings.getLastBooking(), Matchers.nullValue());

        bookingService.confirmationBooking(lastBookingId, ownerId, true);

        assertEquals(nextBookingId, itemService.getByItemId(itemDto.getId(), ownerId).getNextBooking().getId());
        assertEquals(lastBookingId, itemService.getByItemId(itemDto.getId(), ownerId).getLastBooking().getId());

        //by not owner
        assertNull(itemService.getByItemId(itemDto.getId(), ownerId + 1).getLastBooking());
        assertNull(itemService.getByItemId(itemDto.getId(), ownerId + 1).getNextBooking());
        System.out.println(itemService.getByItemId(itemDto.getId(), ownerId));
    }


    @Test
    void testGetByText() {
        ItemDto item = itemService.createItem(6L, list.get(6));

        assertTrue(itemService.getItemByComment("6 deSc").contains(item)
                && itemService.getItemByComment("6 dEsc").size() == 1);
        item.setDescription("updated");
        itemService.updateItem(6L, item);

        assertTrue(itemService.getItemByComment("item6").contains(item)
                && itemService.getItemByComment("item6").size() == 1);
        assertTrue(itemService.getItemByComment("").isEmpty());
        item.setAvailable(false);
        itemService.updateItem(6L, item);

        assertTrue(itemService.getItemByComment("Item6").isEmpty());
    }

    @Test
    void testPostComment() throws InterruptedException {
        ItemDto item = list.get(7);
        long authorId = 7L;
        ItemDto item1 = itemService.createItem(authorId - 1, item);
        String text = "text";
        long itemId = item1.getId();
        CreationBooking bookingDto = new CreationBooking();
        bookingDto.setBookerId(authorId);
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusNanos(100000000));
        bookingDto.setEnd(LocalDateTime.now().plusNanos(120000000));
        BookingDto booking = bookingService.createBooking(bookingDto);
        bookingService.confirmationBooking(booking.getId(), authorId - 1, true);
        Thread.sleep(1000);
        CommentDto actual = itemService.postComment(text, itemId, authorId);

        assertEquals(text, actual.getText());
        assertEquals(userService.getUserById(authorId).getName(), actual.getAuthorName());
        assertNotNull(actual.getCreated());
        assertNotEquals(0, actual.getId());
    }

    @BeforeAll
    void before() {
        for (int i = 1; i <= 10; i++) {
            UserDto userDto = UserDto.builder()
                    .name(String.format("User%d", i))
                    .email(String.format("email%d@mail.net", i))
                    .build();
            userService.createUser(userDto);
        }

        list = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            ItemDto itemDto = ItemDto.builder()
                    .available(true)
                    .name(String.format("Item%d", i))
                    .description(String.format("Item%d description", i))
                    .build();
            list.add(itemDto);
        }
    }

    @Test
    void testGetByUserId() {
        int from = 0;
        int size = 10;

        // Нет товаров
        assertIterableEquals(Collections.emptyList(), itemService.getItemsByUserId(4L, from, size));

        // Один товар
        UserDto user = userService.getUserById(4L);
        ItemDto itemDto = itemService.createItem(4L, list.get(4));
        ItemRequest itemRequest = new ItemRequest();
        Item item = ItemMapper.fromItemDto(itemDto, null, itemRequest); // Используем null вместо user
        List<ItemBookingsDto> expected = new ArrayList<>(List.of(ItemMapper.toItemBookingsDto(item, null, null)));
        Collection<ItemBookingsDto> byUserId = itemService.getItemsByUserId(4L, from, size);

        assertIterableEquals(expected, byUserId);

        // Два товара
        ItemDto itemDto2 = itemService.createItem(4L, list.get(5));
        Item item2 = ItemMapper.fromItemDto(itemDto2, null, itemRequest); // Используем null вместо user
        expected.add(ItemMapper.toItemBookingsDto(item2, null, null));

        assertIterableEquals(expected, itemService.getItemsByUserId(4L, from, size));
    }

    @Test
    void testGetByUserId_InvalidUser() {
        int from = 0;
        int size = 10;

        // Нет такого пользователя
        assertThrows(NotFoundException.class, () -> {
            itemService.getItemsByUserId(999L, from, size);
        });
    }

    @Test
    void testGetItemCommentDto() {
        // Mocking repositories and dependencies
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemMapper itemMapper = Mockito.mock(ItemMapper.class);

        // Creating test data
        Long itemId = 1L;
        Long requestFromUserId = 2L;

        // Mocking repository methods
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty()); // Возвращаем Optional.empty()

        // Running the method under test
        assertThrows(NotFoundException.class, () -> {
            itemService.getByItemId(itemId, requestFromUserId);
        });

        // Verify method calls
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        Mockito.verifyNoInteractions(bookingRepository);
        Mockito.verifyNoInteractions(commentRepository);
        Mockito.verifyNoInteractions(itemMapper);
    }
}
