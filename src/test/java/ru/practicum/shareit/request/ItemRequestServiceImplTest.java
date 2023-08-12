package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemRequestRepository;
import ru.practicum.shareit.item.dto.RequestDto;
import ru.practicum.shareit.item.dto.ShortRequestDto;
import ru.practicum.shareit.item.itemBooking.dto.RequestList;
import ru.practicum.shareit.item.model.ItemRequest;
import ru.practicum.shareit.item.service.ItemRequestServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.Validator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = Validator.class)
class ItemRequestServiceImplTest {

    @Mock
    private Validator validator;
    @InjectMocks
    private ItemRequestServiceImpl service;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    private ShortRequestDto shortDto;

    @BeforeEach
    public void before() {
        shortDto = new ShortRequestDto();
        shortDto.setRequesterId(1);
        shortDto.setDescription("Text");
    }

    @Test
    void testCreate() {
        long requestId = 4;
        long requesterId = shortDto.getRequesterId();
        User requester = new User(requesterId, "mail@mail.ru", "userName");
        LocalDateTime pointBefore = LocalDateTime.now();

        when(userRepository.findById(requesterId)).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(ArgumentMatchers.any(ItemRequest.class)))
                .thenAnswer(invocationOnMock -> {
                    ItemRequest ir = invocationOnMock.getArgument(0, ItemRequest.class);
                    ir.setId(requestId);
                    return ir;
                });
        RequestDto actual = service.createRequests(shortDto);

        LocalDateTime pointAfter = LocalDateTime.now();

        assertEquals(requestId, actual.getId());
        assertEquals(shortDto.getDescription(), actual.getDescription());
        LocalDateTime actualTime = LocalDateTime.parse(actual.getCreated(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        assertTrue(actualTime.isAfter(pointBefore));
        assertTrue(actualTime.isBefore(pointAfter));

        verifyNoMoreInteractions(userRepository, itemRequestRepository);
    }

    @Test
    void testGetAll() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.getAllRequestsBySearcher(1L));
        verifyNoInteractions(itemRequestRepository, itemRepository);
    }

    @Test
    public void getAllRequestsBySearcher_ReturnsListOfRequestListsWithItems() {
        // Arrange

        long requesterId = 1L;
        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");

        // When
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(requesterId, sortByCreated);


        assertEquals(0, itemRequests.size());
    }


    @Test
    void testGetById() {
        ItemRequest expectedRequest = new ItemRequest();
        expectedRequest.setId(3L);
        expectedRequest.setDescription("Text");
        expectedRequest.setCreated(LocalDateTime.now());
        List<Item> expectedItems = List.of(
                Item.builder().id(2).available(true).name("item1").build(),
                Item.builder().id(5).available(false).name("item2").build()
        );

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(expectedRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(expectedItems);

        RequestList actual = service.getRequestById(1, 1);
        assertEquals(expectedRequest.getId(), actual.getId());
        assertEquals(expectedRequest.getCreated().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), actual.getCreated());
        assertEquals(expectedItems.size(), actual.getItems().size());
        assertEquals(actual.getItems().stream().mapToLong(ItemDto::getId).sum(),
                expectedItems.stream().mapToLong(Item::getId).sum());
        verifyNoMoreInteractions(userRepository, itemRequestRepository, itemRepository);
    }
}