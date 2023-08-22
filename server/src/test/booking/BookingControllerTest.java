package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.booking.model.Status.APPROVED;
import static ru.practicum.shareit.booking.model.Status.WAITING;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private static final String X_HEADER_NAME = "X-Sharer-User-Id";

    @Test
    void createBookingEndpointTest() throws Exception {
        User booker = new User(2, "booker", "booker@booker.ru");
        Item item1 = Item.builder().id(1).ownerId(1).name("вещь1").description("описание 1").available(true).build();

        Mockito
                .when(bookingService.create(any(CreateBooking.class), anyLong()))
                .thenAnswer(invocationOnMock -> {
                    CreateBooking createBooking = invocationOnMock.getArgument(0, CreateBooking.class);
                    return new Booking(1, item1, booker, createBooking.getStart(), createBooking.getEnd(), WAITING);
                });

        CreateBooking createBooking = CreateBooking.builder().itemId(1)
                .start(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .end(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(2)).build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBooking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_HEADER_NAME, 1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void approveBookingEndpointTest() throws Exception {
        User booker = new User(2, "booker", "booker@booker.ru");
        Item item1 = Item.builder().id(1).ownerId(1).name("вещь1").description("описание 1").available(true).build();
        Booking approvedBooking = Booking.builder().id(1).item(item1).booker(booker)
                .rentStartDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .rentEndDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .status(APPROVED).build();

        Mockito
                .when(bookingService.approve(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(approvedBooking);

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_HEADER_NAME, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void getBookingEndpointTest() throws Exception {
        User booker = new User(2, "booker", "booker@booker.ru");
        Item item1 = Item.builder().id(1).ownerId(1).name("вещь1").description("описание 1").available(true).build();
        Booking approvedBooking = Booking.builder().id(1).item(item1).booker(booker)
                .rentStartDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .rentEndDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .status(APPROVED).build();

        Mockito
                .when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(approvedBooking);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_HEADER_NAME, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getBookingsByBookerIdEndpointTest() throws Exception {
        User booker = new User(2, "booker", "booker@booker.ru");
        Item item1 = Item.builder().id(1).ownerId(1).name("вещь1").description("описание 1").available(true).build();
        Booking approvedBooking = Booking.builder().id(1).item(item1).booker(booker)
                .rentStartDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .rentEndDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .status(APPROVED).build();

        Mockito
                .when(bookingService.getBookingsByBookerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(approvedBooking));

        mvc.perform(get("/bookings")
                        .param("from", "0")
                        .param("size", "0")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_HEADER_NAME, 2))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(BookingDtoMapper.toBookingDtoList(List.of(approvedBooking)))));
    }

    @Test
    void getBookingsByOwnerIdEndpointTest() throws Exception {
        User booker = new User(2, "booker", "booker@booker.ru");
        Item item1 = Item.builder().id(1).ownerId(1).name("вещь1").description("описание 1").available(true).build();
        Booking approvedBooking = Booking.builder().id(1).item(item1).booker(booker)
                .rentStartDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .rentEndDate(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS).plusHours(1))
                .status(APPROVED).build();

        Mockito
                .when(bookingService.getBookingsByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(approvedBooking));

        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "0")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_HEADER_NAME, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(BookingDtoMapper.toBookingDtoList(List.of(approvedBooking)))));
    }

    @Test
    void getBookingsByOwnerIdEndpointBookingUnsupportedStatusExceptionTest() throws Exception {
        Mockito
                .when(bookingService.getBookingsByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenThrow(new ValidationException("Unknown state: "));

        mvc.perform(get("/bookings/owner")
                        .param("from", "0")
                        .param("size", "0")
                        .param("state", "UNSUPPORTED")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_HEADER_NAME, 1))
                .andExpect(status().isBadRequest());
    }
}