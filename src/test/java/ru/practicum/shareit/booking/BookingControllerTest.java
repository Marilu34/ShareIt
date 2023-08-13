package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreationBooking;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testCreate() throws Exception {

        long bookerId = 5L;
        CreationBooking booking = new CreationBooking();
        BookingDto expected = new BookingDto(5, "1990", "2023", "Status", null, null);
        when(bookingService.createBooking(any(CreationBooking.class))).thenReturn(expected);

        String responseBody = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", String.valueOf(bookerId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking))
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), responseBody);

        booking.setBookerId(bookerId);
        verify(bookingService, times(1)).createBooking(any(CreationBooking.class));
    }


    @SneakyThrows
    @Test
    void testGetBookingById() {
        mockMvc.perform(get("/bookings/{bookingId}", 11)
                        .header("X-Sharer-User-id", 11))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void testGetAllBookingsByOwner() {
        long ownerId = 3L;
        String state = "WAITING";
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-id", ownerId)
                        .queryParam("state", state))
                .andExpect(status().isOk());
    }


    @SneakyThrows
    @Test
    void testGetAllBookingsByBooker() {
        DateTimeFormatter localDateTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        long bookerId = 3L;
        State state = State.APPROVED;
        int from = 10;
        int size = 5;
        List<BookingDto> expected = List.of(
                new BookingDto(1,
                        LocalDateTime.now().minusDays(1).format(localDateTime),
                        LocalDateTime.now().minusHours(23).format(localDateTime),
                        Status.APPROVED.name(),
                        null,
                        null),
                new BookingDto(3,
                        LocalDateTime.now().minusDays(2).format(localDateTime),
                        LocalDateTime.now().minusHours(47).format(localDateTime),
                        Status.APPROVED.name(),
                        null,
                        null)
        );
        when(bookingService.getAllBookingsByBooker(bookerId, state, from, size))
                .thenReturn(expected);

        String responseBody = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-id", bookerId)
                        .queryParam("state", state.name())
                        .queryParam("from", Integer.toString(from))
                        .queryParam("size", Integer.toString(size)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertArrayEquals(expected.toArray(),
                objectMapper.readValue(responseBody, BookingDto[].class));

        verify(bookingService, times(1))
                .getAllBookingsByBooker(bookerId, state, from, size);

    }

    @SneakyThrows
    @Test
    void testConfirmationBooking() {
        long ownerId = 11L;
        long bookingId = 12L;
        boolean approved = true;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-id", ownerId)
                        .queryParam("approved", Boolean.toString(approved))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bookingService, times(1)).confirmationBooking(bookingId, ownerId, approved);
    }


    @Test
    void testBadCreate() throws Exception {
        CreationBooking dto = new CreationBooking();
        mockMvc.perform(post("/bookings")
                        .header("OtherHeader", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError());
        verifyNoInteractions(bookingService);
    }

    @Test
    void testGetBookingsWithoutParms() throws Exception {
            long ownerId = 3L;
            State defaultState = State.ALL;
            int defaultFrom = 0;
            int defaultSize = 10;

            MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/bookings/owner")
                    .header("X-Sharer-User-id", String.valueOf(ownerId));

            mockMvc.perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk());

            verify(bookingService, times(1)).getAllBookingsByOwner(ownerId, defaultState, defaultFrom, defaultSize);
        }



        @Test
    void testBadGetAllBookings() throws Exception {
        long ownerId = 3L;
        String state = "WRONG_STATE";
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-id", ownerId)
                        .queryParam("state", state))
                .andExpect(status().is4xxClientError());
    }
}